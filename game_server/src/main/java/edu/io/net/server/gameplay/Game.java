package edu.io.net.server.gameplay;

import edu.io.net.command.*;
import edu.io.net.server.GameServer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Game implements UpdateStateFactory.StateSource {
    private Thread thread;
    public final long gameStartedTimestamp = System.currentTimeMillis();
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final Board board;
    private GameServer gameSrv;

    public Game() {
        UpdateStateFactory.register(this);
        board = new Board();
    }

    public Command.Status addPlayer(
            @NotNull String clientId,
            @NotNull Player player) {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(player);
        // TODO: check name not clientId!
        if (null != players.putIfAbsent(clientId, player)) {
            return JoinGame.CmdRe.Status.ALREADY_CONNECTED;
        }
        return JoinGame.CmdRe.Status.OK;
    }

    public Command.Status removePlayer(
            @NotNull String clientId) {
        Objects.requireNonNull(clientId);
        if (null == players.remove(clientId)) {
            return LeaveGame.CmdRe.Status.CLIENT_NOT_CONNECTED;
        }
        return LeaveGame.CmdRe.Status.OK;
    }

    /**
     * Start the game
     */
    public void start(GameServer gameSrv) {
        this.gameSrv = Objects.requireNonNull(gameSrv);
        thread = Thread.startVirtualThread(() -> {

        });
    }

    @Override
    public void populateStatePack(UpdateState.Cmd cmd) {
    }
}
