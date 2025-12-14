package edu.io.net.client;

import edu.io.net.command.GameState;

public class Board {
    private int size = 0;
    private String[][] grid;
    private static final String EMPTY_LABEL = "・";

    public void create(GameState.BoardInfo boardInfo) {
        size = boardInfo.size();
        grid = new String[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[col][row] = EMPTY_LABEL;
            }
        }
    }

    public void setSquare(GameState.BoardSquareInfo squareInfo) {
        var pos = squareInfo.pos();
        grid[pos.col()][pos.row()] = squareInfo.label();
    }

    public void display() {
        System.out.println("\n");
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.printf("%s", grid[col][row]);
            }
            System.out.println();
        }
    }

}
