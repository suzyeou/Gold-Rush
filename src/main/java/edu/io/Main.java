package edu.io;

import edu.io.token.PlayerToken;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Gold Rush");
        Board board = new Board();
        PlayerToken player = new PlayerToken(board);
        Scanner scanner = new Scanner(System.in);

        board.display();

        while (true) {
            System.out.println("Available moves: UP, DOWN, LEFT, RIGHT, NONE");
            System.out.print("Enter your move: ");
            String direction = scanner.nextLine().toUpperCase();

            try {
                player.move(PlayerToken.Move.valueOf(direction));
            } catch (IllegalArgumentException e) {
                System.out.println("Cannot move outside the board");
            }

            board.display();
        }
    }
}
