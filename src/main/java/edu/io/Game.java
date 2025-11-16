package edu.io;

import edu.io.player.Player;
import edu.io.token.*;
import java.util.Objects;
import java.util.Scanner;

public class Game {
    private Board board;
    private Player player;

    public Game() {
        board = new Board();
    }

    public void join(Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");;
        PlayerToken playerToken = new PlayerToken(player, board);
        player.assignToken(playerToken);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        board.placeToken(5, 9, new PickaxeToken());
        board.placeToken(8, 7, new WaterToken(2));
        board.placeToken(3, 4, new GoldToken());
        board.placeToken(1, 6, new AnvilToken());
        board.placeToken(9, 5, new PyriteToken());
        board.display();

        while (true) {
            System.out.println("Moves: W (UP), S (DOWN), A (LEFT), D (RIGHT), E (NONE)");
            System.out.print("Enter your move: ");
            String direction = scanner.nextLine().toUpperCase();

            try {
                switch (direction) {
                    case "W": player.token().move(PlayerToken.Move.UP); break;
                    case "S": player.token().move(PlayerToken.Move.DOWN); break;
                    case "A": player.token().move(PlayerToken.Move.LEFT); break;
                    case "D": player.token().move(PlayerToken.Move.RIGHT); break;
                    case "E": player.token().move(PlayerToken.Move.NONE); break;
                    default: System.out.println("Invalid direction."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Cannot move outside the board");
            }

            board.display();
        }
    }
}
