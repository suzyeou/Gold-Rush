package edu.io.net.client;

import edu.io.net.command.*;

import java.util.Scanner;

import static java.lang.System.exit;

public class GameClient {
    public static void main(String[] args) {
        new GameClient().start();
    }

    private final Game game = new Game();
//
    private void start() {
        var gsc = new GameServerConnector(
                "tcp://localhost:1313",
                new SocketConnector());
        gsc.connect()
                .onFailure(() -> {
                    System.err.println("Failed to connect to server");
                    exit(0);
                });
        gsc.issueCommand(new Handshake.Cmd("1.2"), cmdRe -> {
            if (cmdRe.status() != Handshake.CmdRe.Status.OK) {
                System.err.printf("Error: %s",cmdRe.status());
                exit(0);
            }
        });

        gsc.onCommandFromServer(cmd -> switch (cmd) {
            case UpdateState.Cmd updateCmd-> doUpdateState(updateCmd);
            default -> CommandAck.NO_ACK;
        });

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.next();
        gsc.issueCommand(new JoinGame.Cmd(name), cmdRe -> {
            if (cmdRe.status() != JoinGame.CmdRe.Status.OK) {
                System.err.printf("Error: %s",cmdRe.status());
                exit(0);
            }
        });

        new Scanner(System.in).nextLine();
    }

    private CommandAck doUpdateState(UpdateState.Cmd cmd) {
        var status = UpdateState.CmdRe.Status.OK;
        cmd.forEach(stateInfo -> {
            switch (stateInfo) {
                case GameState.BoardInfo boardInfo ->
                        game.board.create(boardInfo);
                case GameState.BoardSquareInfo squareInfo ->
                        game.board.setSquare(squareInfo);
                default -> {}
            }
        });
        game.redraw();
        return new CommandAck(cmd, new UpdateState.CmdRe(status));
    }
}
