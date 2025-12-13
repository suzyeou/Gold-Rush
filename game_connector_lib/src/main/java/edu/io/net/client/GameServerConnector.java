package edu.io.net.client;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.io.net.command.Command;
import edu.io.net.command.CommandAck;
import edu.io.net.command.CommandRe;
import org.jetbrains.annotations.NotNull;

/**
 * High-level networking connector used by the game client to communicate
 * with the remote game server.
 *
 * <p>This class does not perform low-level socket operations directly.
 * Instead, it delegates them to an injected {@link NetworkConnector},
 * while providing a convenient high-level API for:
 *
 * <ul>
 *     <li>opening and closing connections,</li>
 *     <li>sending commands with or without awaiting responses,</li>
 *     <li>dispatching responses matched by command ID,</li>
 *     <li>handling server-initiated commands,</li>
 *     <li>tracking success/failure of the most recent operation.</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * All incoming commands are dispatched on the thread provided by
 * {@link NetworkConnector}. The map of pending responses is thread-safe,
 * but any state modified inside callback handlers must be protected by
 * the caller.
 *
 * <h2>Lifecycle</h2>
 * Typical usage:
 *
 * <pre>{@code
 * GameServerConnector connector = new GameServerConnector("user@localhost:1313");
 *
 * connector
 *     .connect()
 *     .onSuccess(() -> System.out.println("Connected!"))
 *     .onFailure(() -> System.err.println("Failed to connect."));
 *
 * connector.issueCommand(new Echo.Cmd("hello"), response -> {
 *     System.out.println("Received: " + response);
 * });
 * }</pre>
 */
public class GameServerConnector {
    private final URI uri;
    private final NetworkConnector netConnector;

    private State state;
    private Status status;

    /** Pending commands awaiting a response matched by command ID. */
    private final ConcurrentHashMap<Long, Consumer<CommandRe>>
            pendingCommands = new ConcurrentHashMap<>();

    /** Handler for commands sent by the server. */
    private @NotNull Function<Command, CommandAck> cmdSentBySrvHandler =
            (arg) -> CommandAck.NO_ACK;

    /** Handler invoked for any received response from the server. */
    private @NotNull Consumer<CommandRe> respFromSrvHandler =
            (arg) -> {};

    private enum State { DISCONNECTED, CONNECTED }
    private enum Status { NONE, OK, ERROR }

    /**
     * Creates a {@code GameServerConnector} with a stub no-op network
     * connector (useful for testing or offline runs).
     *
     * <p>Expected connection string format:
     * {@code userid@host:port}
     *
     * @param connStr connection string representing the server location
     */
    public GameServerConnector(@NotNull String connStr) {
        this(connStr, new DumbNetworkConnector());
    }

    /**
     * Creates a {@code GameServerConnector} with a custom network
     * connector implementation.
     *
     * <p>The connection string must include a host and a positive port.
     * User info is accepted but ignored.
     *
     * @param connString RFC-2396 compliant URI-like connection string
     * @param netConnector low-level connector performing actual I/O
     * @throws IllegalArgumentException if the connection string is invalid
     */
    public GameServerConnector(
            @NotNull String connString,
            @NotNull NetworkConnector netConnector) {

        this.uri = parseAndValidateConnString(connString);
        this.state = State.DISCONNECTED;
        this.status = Status.NONE;
        this.netConnector = netConnector;
    }

    /**
     * Returns whether the connector is currently connected to the server.
     *
     * @return {@code true} if connected, otherwise {@code false}
     */
    public boolean isConnected() {
        return state == State.CONNECTED;
    }

    /**
     * Attempts to establish a connection to the server.
     *
     * <p>Registers the internal dispatcher that handles all incoming
     * command objects:
     * <ul>
     *     <li>If a command matches a pending command ID,
     *         its response handler is executed.</li>
     *     <li>Otherwise, the command is considered server-initiated
     *         and dispatched to {@link #onCommandFromServer(Function)}.</li>
     * </ul>
     *
     * <p>On failure, no exception is thrown; the connector transitions
     * to {@code DISCONNECTED} state and the operation status becomes
     * {@code ERROR}.
     *
     * @return this connector for fluent API usage
     */
    public GameServerConnector connect() {
        try {
            netConnector.connect(uri);

            netConnector.onAnyCmdFromServer(fromSrv -> {
                if (pendingCommands.remove(fromSrv.id()) instanceof
                                            Consumer<CommandRe> onResponse) {
                    // Matched response to a previously sent command
                    if (fromSrv instanceof CommandAck cmdAck) {
                        var cmdRe = cmdAck.resCmd();
                        onResponse.accept(cmdRe);
                        respFromSrvHandler.accept(cmdRe);
                    }
                }
                else {
                    // Command sent by the server
                    var ack = cmdSentBySrvHandler.apply(fromSrv);
                    if (ack != null && ack != CommandAck.NO_ACK) {
                        issueCommand(ack);
                    }
                }
            });

            state = State.CONNECTED;
            status = Status.OK;
        }
        catch (IOException e) {
            state = State.DISCONNECTED;
            status = Status.ERROR;
        }
        return this;
    }

    /**
     * Closes the connection to the server.
     *
     * <p>If an error occurs, the exception is suppressed; state remains
     * {@code DISCONNECTED} and status becomes {@code ERROR}.
     *
     * @return this connector for fluent chaining
     */
    public GameServerConnector disconnect() {
        try {
            netConnector.disconnect();
            status = Status.OK;
        }
        catch (IOException e) {
            status = Status.ERROR;
        }

        state = State.DISCONNECTED;
        return this;
    }

    /**
     * Sends a command to the server without registering any response
     * handler.
     *
     * <p>On failure, the exception is wrapped in a
     * {@code RuntimeException} and {@code status} becomes
     * {@code ERROR}.
     *
     * @param cmd command to send
     * @return this connector
     * @throws RuntimeException if the send operation fails
     */
    public GameServerConnector issueCommand(@NotNull Command cmd) {
        try {
            netConnector.sendToServer(cmd);
            status = Status.OK;
        }
        catch (Exception e) {
            status = Status.ERROR;
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Sends a command to the server and registers a callback to be
     * invoked when a command with the same ID is received.
     *
     * <p>If sending fails, the callback is not registered and the
     * failure is propagated as a {@code RuntimeException}.
     *
     * @param cmd command to send
     * @param onResponse handler for the matching response
     * @return this connector
     * @throws RuntimeException if the send operation fails
     */
    public GameServerConnector issueCommand(
            @NotNull Command cmd,
            @NotNull Consumer<CommandRe> onResponse) {
        issueCommand(cmd);
        pendingCommands.put(cmd.id(), onResponse);
        return this;
    }

    /**
     * Registers a handler for commands initiated by the server.
     *
     * <p>The handler may return:
     * <ul>
     *     <li>{@code CommandAck.NO_ACK} — no automatic reply is sent.</li>
     *     <li>any other {@link CommandAck} — it will be automatically
     *         sent back to the server by invoking
     *         {@link #issueCommand(Command)}.</li>
     * </ul>
     *
     * @param onCommand callback invoked for all unsolicited server commands
     */
    public void onCommandFromServer(
            @NotNull Function<Command, CommandAck> onCommand) {
        this.cmdSentBySrvHandler = Objects.requireNonNullElse(
                onCommand,
                (arg) -> CommandAck.NO_ACK
        );
    }

    /**
     * Registers a handler invoked whenever a response (matched by ID)
     * is received from the server.
     *
     * <p>This handler is invoked <em>after</em> the individual response
     * callback supplied via
     * {@link #issueCommand(Command, Consumer)}.
     *
     * @param onResponse consumer receiving all responses
     * @return this connector
     */
    public GameServerConnector onResponseFromServer(
            @NotNull Consumer<CommandRe> onResponse) {
        this.respFromSrvHandler = Objects.requireNonNull(onResponse);
        return this;
    }

    /**
     * Executes the provided action if the last operation succeeded.
     *
     * <p>After invocation (whether triggered or not) the internal status
     * resets to {@code NONE}.
     *
     * @param action code to run on success
     * @return this connector
     */
    public GameServerConnector onSuccess(@NotNull Runnable action) {
        if (status == Status.OK) {
            status = Status.NONE;
            action.run();
        }
        return this;
    }

    /**
     * Executes the provided action if the last operation failed.
     *
     * <p>After invocation (whether triggered or not) the internal status
     * resets to {@code NONE}.
     *
     * @param action code to run on failure
     * @return this connector
     */
    public GameServerConnector onFailure(@NotNull Runnable action) {
        if (status == Status.ERROR) {
            status = Status.NONE;
            action.run();
        }
        return this;
    }

    // ---------------------------------------------------------------------
    // Stub connector used when no real NetworkConnector is supplied
    // ---------------------------------------------------------------------

    private static class DumbNetworkConnector implements NetworkConnector {
        @Override public void connect(URI uri) throws IOException {}
        @Override public void disconnect() throws IOException {}
        @Override public void sendToServer(Command cmd) throws IOException {}
        @Override public void onAnyCmdFromServer(Consumer<Command> cmd) {}
    }

    /**
     * Parses and validates the connection string.
     *
     * <p>Required:
     * <ul>
     *     <li>non-null host</li>
     *     <li>port &gt; 0</li>
     * </ul>
     *
     * <p>User info is ignored, but allowed.
     *
     * @param connString connection string to parse
     * @return a validated URI instance
     * @throws IllegalArgumentException if the string is malformed or missing required components
     */
    private static URI parseAndValidateConnString(String connString) {
        try {
            var uri = URI.create(connString);
            if (uri.getHost() == null || uri.getPort() <= 0) {
                throw new IllegalArgumentException();
            }
            return uri;
        }
        catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Invalid connection string", e);
        }
    }
}
