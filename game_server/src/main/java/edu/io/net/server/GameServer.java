package edu.io.net.server;

import edu.io.net.Version;
import edu.io.net.command.*;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import edu.io.net.server.gameplay.Game;
import edu.io.net.server.gameplay.Player;
import edu.io.net.server.tcp.Client;
import edu.io.net.server.tcp.TCPServer;
import static edu.io.net.command.GameState.Pack.AFTER_JOIN_GAME;
import static edu.io.net.command.GameState.Pack.PLAYERS_LIST;

/**
 * High-level game server coordinating domain-level game logic and
 * handling game-related commands delivered through a lower-level
 * {@link TCPServer}.
 *
 * <p>The server acts as the domain layer of the architecture. It does
 * <strong>not</strong> manage sockets directly; instead it delegates
 * all transport responsibilities—such as accepting connections,
 * receiving commands and sending responses—to {@link TCPServer}.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>maintaining the {@link Game} instance and executing game logic,</li>
 *   <li>registering handlers for all supported command types,</li>
 *   <li>processing commands in a per-client context,</li>
 *   <li>sending responses or broadcasting game state updates,</li>
 *   <li>validating protocol versions and command correctness.</li>
 * </ul>
 *
 * <h2>Threading model</h2>
 * All command handlers run on virtual threads created by
 * {@link TCPServer}, one per connected client. Game state may therefore
 * be modified concurrently and must be thread-safe.
 *
 * <h2>State synchronization</h2>
 * Handlers may broadcast {@link Command} instances (such as game state
 * updates) using {@link TCPServer#broadcast(Command)} to keep all
 * clients synchronized.
 */
public class GameServer {
    private final TCPServer server;
    private Game game;

    private static Logger log() {
        return LoggerFactory.getLogger(GameServer.class);
    }

    /**
     * Entry point for running the server as a standalone application.
     *
     * <p>Creates a new {@code GameServer} bound to port {@code 1313},
     * assigns a fresh {@link Game} instance, and starts the server.
     * The server begins accepting TCP connections and dispatching incoming
     * commands to registered handlers.
     *
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
//        TODO: add cmd-line argument -p for port; default is 1313
        new GameServer(1313)
                .assignGame(new Game())
                .start();
    }

    /**
     * Creates a new game server instance bound to the given TCP port.
     *
     * <p>The constructor initializes:
     * <ul>
     *   <li>a {@link TCPServer} responsible for transport-level handling,</li>
     *   <li>command routing via {@link #setupCommandRouter()}.</li>
     * </ul>
     *
     * <p>A game instance is not created here; it must be provided through
     * {@link #assignGame(Game)} before calling {@link #start()}.
     *
     * @param port TCP port on which the server will listen for client connections
     */
    public GameServer(int port) {
        server = new TCPServer(port);
        setupCommandRouter();
    }

    /**
     * Assigns the {@link Game} instance used by this server.
     *
     * <p>This method must be called before {@link #start()} or before
     * handling commands requiring game access.
     *
     * @param game non-null game instance
     * @return this server instance for method chaining
     */
    public GameServer assignGame(Game game) {
        this.game = Objects.requireNonNull(game);
        return this;
    }

    /**
     * Starts the game logic using the currently assigned {@link Game}
     * instance.
     *
     * <p>This does not start the TCP subsystem; the transport layer is
     * started by {@link #start()}.
     */
    public void startGame() {
        game.start(this);
    }

    /**
     * Starts the underlying {@link TCPServer}, enabling client acceptance
     * and command dispatch.
     *
     * <p>The method attempts to start the TCP subsystem and propagates
     * startup failures as a {@link RuntimeException}. Game logic should be
     * initialized beforehand via {@link #assignGame(Game)} and (optionally)
     * {@link #startGame()}.
     *
     * <p>Once running, the server processes incoming commands on virtual
     * threads created per client connection.
     *
     * @return this server instance for method chaining
     */
    public GameServer start() {
        try {
            server.start();
        } catch (Exception e) {
            log().error("Failed to start server", e);
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Registers handlers for all supported command types with the
     * underlying {@link TCPServer}.
     *
     * <p>Each incoming {@link Command} instance is matched to the
     * appropriate handler method. Unknown command types are logged and
     * acknowledged with {@link CommandAck#NO_ACK}.
     *
     * <p>This method effectively connects domain-level logic with the
     * transport layer and must be invoked before starting the server.
     */
    private void setupCommandRouter() {
        server.onCommand((client, reqCmd) ->
            switch (reqCmd) {
                case Echo.Cmd cmd -> doEcho(cmd);
                case Handshake.Cmd cmd -> doHandshake(cmd);
                case JoinGame.Cmd cmd -> doJoinGame(client, cmd);
                case LeaveGame.Cmd cmd -> doLeaveGame(client, cmd);
                case GetInfo.Cmd cmd -> doGetInfo(client, cmd);
                default -> {
//                    log().error("Unknown command: [{}] {}", reqCmd.id(), reqCmd);
                    log().error("Unknown command: {}", reqCmd);
                    yield CommandAck.NO_ACK;
                }
            }
        );
    }

    /**
     * Handles an incoming handshake request from a newly connected client.
     *
     * <p>Validates the connector library version supplied by the client.
     * Expected format: {@code MAJOR.MINOR[.PATCH]}, e.g. {@code 1.4} or
     * {@code 2.0.3}.
     *
     * <p>The method checks whether the client's version is compatible with
     * the server-side protocol version specified in {@link Version}.
     *
     * @param cmd handshake request
     * @return acknowledgment describing the result of the version check
     */
    private @NotNull CommandAck doHandshake(@NotNull Handshake.Cmd cmd) {
        var status = Handshake.CmdRe.Status.OK;
        var vs = Objects.requireNonNullElse(cmd.connectorVersion, "");
        Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.\\d+)?$");
        Matcher matcher = pattern.matcher(vs);
        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            if (major < Version.MAJOR ||
                    (major == Version.MAJOR && minor < Version.MINOR)) {
                status = Handshake.CmdRe.Status.LIB_VERSION_TOO_LOW;
            }
        }
        else {
            status = Handshake.CmdRe.Status.LIB_VERSION_MALFORMED;
        }
        return new CommandAck(cmd, new Handshake.CmdRe(status));
    }

    /**
     * Handles an echo request by returning the same message back to the
     * client. Useful for connectivity checks or debugging.
     *
     * @param cmd echo command
     * @return acknowledgment containing the echoed message
     */
    private @NotNull CommandAck doEcho(@NotNull Echo.Cmd cmd) {
        return new CommandAck(cmd, new Echo.CmdRe(CommandRe.Status.OK, cmd.msg));
    }

    /**
     * Attempts to register the requesting client as a player in the active
     * game session.
     *
     * <p>A unique player ID is generated based on the client's underlying
     * socket. If registration succeeds, the ID is stored in the
     * {@link Client} object and a game-state update is sent to the client.
     *
     * @param client the requesting client
     * @param cmd join-game command
     * @return acknowledgment containing join status and assigned player ID
     */
    private @NotNull CommandAck doJoinGame(
            @NotNull Client client,
            @NotNull JoinGame.Cmd cmd) {
        var clientId = "%x".formatted(client.socket().hashCode());
        var status = game.addPlayer(clientId, new Player(cmd));
        if (status == JoinGame.CmdRe.Status.OK) {
            client.setId(clientId);
            server.sendToClient(client, UpdateStateFactory.create(AFTER_JOIN_GAME));
//            server.broadcast(UpdateStateFactory.create(PLAYERS_LIST));
        }
        return new CommandAck(cmd, new JoinGame.CmdRe(status, clientId));
    }

    /**
     * Removes the requesting client from the game session.
     *
     * <p>If the client is not registered, the response contains
     * {@code CLIENT_NOT_CONNECTED}. On successful removal, a broadcast of
     * the updated player list is triggered.
     *
     * @param client the leaving client
     * @param cmd leave-game command
     * @return acknowledgment containing the result of the leave operation
     */
    private @NotNull CommandAck doLeaveGame(
            @NotNull Client client,
            @NotNull LeaveGame.Cmd cmd) {
        Command.Status status = LeaveGame.CmdRe.Status.CLIENT_NOT_CONNECTED;
        if (server.getClientBySocket(client.socket()).isPresent()) {
            status = game.removePlayer(client.id());
            if (status == LeaveGame.CmdRe.Status.OK) {
                server.broadcast(UpdateStateFactory.create(PLAYERS_LIST));
            }
        }
        return new CommandAck(cmd, new LeaveGame.CmdRe(status));
    }
    /**
     * Returns information about the requesting client and global game
     * status.
     *
     * <p>If the client has no assigned ID (has not joined the game), the
     * result contains {@code CLIENT_NOT_FOUND}.
     *
     * <p>On success the response includes:
     * <ul>
     *   <li>client identifier,</li>
     *   <li>game start timestamp,</li>
     *   <li>client connection timestamp.</li>
     * </ul>
     *
     * @param client client requesting info
     * @param cmd info request command
     * @return acknowledgment containing the info object
     */
    private @NotNull CommandAck doGetInfo(
            @NotNull Client client,
            @NotNull GetInfo.Cmd cmd) {
        Command.Status status;
        GetInfo.CmdRe.Info info;
        if (client.id().isBlank()) {
            status = CommandRe.Status.CLIENT_NOT_FOUND;
            info = GetInfo.CmdRe.Info.EMPTY;
        }
        else {
            status = CommandRe.Status.OK;
            info = new GetInfo.CmdRe.Info(
                    client.id(),
                    game.gameStartedTimestamp,
                    client.timestamp()
            );
        }
        return new CommandAck(cmd,
                new GetInfo.CmdRe(status, info));
    }

}
