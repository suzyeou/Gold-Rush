package edu.io.net.client;

public class Game {

    public final Board board = new Board();

    public void redraw() {
        System.out.println("-------------------------------------------------");
        board.display();
    }
}
