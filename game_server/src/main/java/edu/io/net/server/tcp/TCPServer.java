package edu.io.net.server.tcp;

import edu.io.net.command.*;
import edu.io.net.server.GameServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Low-level TCP server responsible for accepting socket connections,
 * handling raw client I/O and routing incoming {@link Command}
 * objects to a higher-level command executor.
 *
 * <p>This class forms the transport layer of the architecture.
 * It does <strong>not</strong> interpret or process commands itself;
 * instead, it delegates all command logic to a handler registered via
 * {@link #onCommand(BiFunction)}. Each connected client is represented
 * by a {@link Client} instance that encapsulates input/output streams,
 * a per-client semaphore and metadata such as connection timestamps.
 *
 * <h2>Threading model</h2>
 * <ul>
 *   <li>The server listens on the configured port using a blocking
 *       {@link ServerSocket}.</li>
 *   <li>Each accepted {@link Socket} spawns a new virtual thread
 *       (`Thread.startVirtualThread`) responsible for communicating
 *       with one client.</li>
 *   <li>Within each client thread, incoming serialized objects are read
 *       and passed to the configured command executor.</li>
 *   <li>Outgoing messages are serialized using a per-client semaphore
 *       to prevent interleaving of {@link ObjectOutputStream} writes.</li>
 * </ul>
 *
 * <h2>Command dispatching</h2>
 * Command routing is defined by the function
 * {@code BiFunction<Client, Command, CommandAck>} provided through
 * {@link #onCommand(BiFunction)}. If no executor is registered,
 * all incoming messages produce {@link CommandAck#NO_ACK}.
 *
 * <h2>Client management</h2>
 * Active clients are stored in a thread-safe {@link ConcurrentHashMap}
 * keyed by their underlying {@link Socket}. The map is used for
 * broadcasting messages and for higher-level game logic to associate
 * server state with each client.
 *
 * <h2>Error handling</h2>
 * When a client disconnects or an I/O error occurs, the corresponding
 * client thread terminates. The disconnect is logged, and all further
 * communication attempts for that client quietly stop.
 *
 * <h2>Intended usage</h2>
 * This class is expected to be used as a supporting transport layer
 * underneath a higher-level server (e.g., {@link GameServer}) which
 * defines actual command semantics and game logic.
 */
public class TCPServer {

    private final int port;
    private final Map<Socket, Client> clients = new ConcurrentHashMap<>();
    private BiFunction<Client, Command, CommandAck> executor;
    private final ConcurrentHashMap<Long, BiConsumer<Client, CommandRe>>
            pendingCommands = new ConcurrentHashMap<>();

    private static Logger log() {
        return LoggerFactory.getLogger(GameServer.class);
    }

    /**
     * Creates a new TCP server bound to the specified port.
     * Until a command executor is registered via {@link #onCommand},
     * all incoming client messages will result in {@link CommandAck#NO_ACK}.
     *
     * @param port TCP port the server should listen on
     */
    public TCPServer(int port) {
        this.port = port;
        executor = (_, _) -> CommandAck.NO_ACK;
    }

    /**
     * Retrieves the {@link Client} instance associated with the given
     * {@link Socket}, if it exists.
     *
     * <p>This method allows looking up a connected client based on its
     * underlying socket. If the socket is not currently registered with
     * the server, an empty {@link Optional} is returned.
     *
     * @param socket the socket used to identify the client
     * @return an {@link Optional} containing the associated {@link Client},
     *         or {@link Optional#empty()} if no client is found for the
     *         given socket
     */
    public Optional<Client> getClientBySocket(Socket socket) {
        return Optional.ofNullable(clients.get(socket));
    }

    /**
     * Registers the command executor function invoked for every
     * incoming {@link Command} received from any connected client.
     *
     * <p>The provided function must be thread-safe, as it may be
     * invoked concurrently from multiple client threads.
     *
     * @param executeCommand a function mapping (client, command)
     *                       to a {@link CommandAck} response
     */
    public void onCommand(
            BiFunction<Client, Command, CommandAck> executeCommand
    ) {
        this.executor = Objects.requireNonNull(executeCommand);
    }

    /**
     * Starts the TCP server and begins accepting incoming client
     * connections.
     *
     * <p>For each accepted {@link Socket}, a new {@link Client} wrapper
     * is created and a dedicated virtual thread is spawned to handle
     * all communication with that client.
     *
     * <p>This method blocks until the server socket is closed or an
     * unrecoverable I/O error occurs.
     *
     * @throws IOException if the server socket cannot be opened or an
     *                     error occurs while accepting connections
     */
    public void start() throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            log().info("Server started on port {}", port);
            try {
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    var client = new Client(socket);
                    log().info("Accepted connection from {}",
                            socket.getLocalSocketAddress());
                    clients.put(socket, client);
                    Thread.startVirtualThread(() -> handleClient(client));
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    log().error("Error accepting client connection", e);
                }
            }
        }
    }

    /**
     * Handles all communication with a single client.
     * <p>
     * This method is executed inside a dedicated virtual thread created
     * during the connection accept loop.
     *
     * <p>Processing loop:
     * <ol>
     *   <li>Read a serialized object from the client's input.</li>
     *   <li>If it is a {@link Command}, pass it to the registered
     *       executor via {@link BiFunction#apply(Object, Object)}.</li>
     *   <li>Serialize and send the resulting {@link CommandAck} back to
     *       the client (writes synchronized via the client's semaphore).</li>
     *   <li>Repeat until the client disconnects or an exception is thrown.</li>
     * </ol>
     *
     * <p>Any exception during read/write is interpreted as a client
     * disconnect and terminates the loop.
     *
     * @param client the client associated with this handler thread
     */
    private void handleClient(@NotNull Client client) {
        try (var out = client.out(); var in = client.in()) {
            while (true) {
                try {
                    if (in.readObject() instanceof Command cmd) {
                        if (pendingCommands.remove(cmd.id()) instanceof
                                    BiConsumer<Client, CommandRe> onResponse) {
                            // response from client
                            if (cmd instanceof CommandAck ack) {
                                var cmdRe = ack.resCmd();
//                                log().info("Client res: [{}] {}", cmd.id(), cmdRe);
                                log().info("Client res: {}", cmdRe);
                                onResponse.accept(client, cmdRe);
                            }
                        }
                        else {
                            // request from client
//                            log().info("Req: [{}] {}", cmd.id(), cmd);
                            log().info("Req: {}", cmd);
                            client.semaphore().acquire();
                            var res = executor.apply(client, cmd);
                            if (res != CommandAck.NO_ACK) {
                                out.writeObject(res);
                                out.flush();
                            }
                            client.semaphore().release();
//                            log().info("Res: [{}] {}", res.id(), res.resCmd());
                            log().info("Res: {}", res.resCmd());
                        }
                    }
                } catch (Exception e) {
                    log().warn("Client disconnected: {}", client.id());
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a command to the given client without registering any response
     * handler.
     *
     * <p>This is a convenience overload equivalent to calling
     * {@link #sendToClient(Client, Command, BiConsumer)} with a
     * no-op response consumer.
     *
     * @param client the target client
     * @param cmd    the command to send
     */
    public void sendToClient(Client client, Command cmd) {
        sendToClient(client, cmd, (_,_) -> {});
    }

    /**
     * Sends the given command asynchronously to a specific client.
     *
     * <p>The send operation is executed on a newly created virtual thread.
     * All writes to the client's {@link ObjectOutputStream} are serialized
     * via the client's internal semaphore to prevent concurrent writes
     * that could corrupt the stream.
     *
     * <p>If the client later responds with a {@link CommandAck} containing a
     * {@link CommandRe}, that response is matched by command ID and delivered
     * to the provided {@code onResponse} handler.
     *
     * <p>If an I/O error occurs during sending, the failure is silently
     * ignored — the assumption being that the client has disconnected and
     * the handler thread will already clean up.
     *
     * @param client      the client to send the command to
     * @param cmd         the command to send
     * @param onResponse  callback invoked when a matching {@link CommandRe}
     *                    is later received from the client
     */
    public void sendToClient(
            Client client,
            Command cmd,
            @NotNull BiConsumer<Client, CommandRe> onResponse
    ) {
        Thread.startVirtualThread(() -> {
            try {
                var out = client.out();
                client.semaphore().acquire();
                out.writeObject(cmd);
                out.flush();
//                log().info("Send: [{}] {}", cmd.id(), cmd);
                log().info("Send: {}", cmd);
                client.semaphore().release();

                pendingCommands.put(cmd.id(), onResponse);
            }
            catch (Exception ignore) {}
        });
    }

    /**
     * Broadcasts the given command to all currently connected clients,
     * ignoring all responses.
     *
     * <p>This is a convenience overload of
     * {@link #broadcast(Command, BiConsumer)} using a no-op response handler.
     *
     * @param cmd the command to broadcast to all clients
     */
    public void broadcast(Command cmd) {
        broadcast(cmd, (_,_) -> {});
    }

    /**
     * Broadcasts the given command to all currently connected clients.
     *
     * <p>Each client receives the message asynchronously via
     * {@link #sendToClient(Client, Command, BiConsumer)}. The broadcast
     * operation never blocks the calling thread.
     *
     * <p>Any {@link CommandRe} responses sent back by individual clients
     * are routed to the provided {@code onResponse} consumer together with
     * the client that sent the response.
     *
     * @param cmd         the command to broadcast
     * @param onResponse  handler invoked for each client response
     */
    public void broadcast(
            Command cmd,
            @NotNull BiConsumer<Client, CommandRe> onResponse
    ) {
        for (var client : clients.values()) {
            sendToClient(client, cmd, onResponse);
        }
    }
}