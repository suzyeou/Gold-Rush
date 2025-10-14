package edu.io;

public class Main {
    public static void main(String[] args) {
        System.out.println("Gold Rush");

        Board board = new Board(10);
        Token gold = new Token("\uD83D\uDCB0");
        Token player = new Token("웃");

        board.placeToken(4, 2, gold);
        board.placeToken(5, 1, player);
        board.display();
    }
}
