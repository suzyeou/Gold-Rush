package edu.io;

public class Board {
    public final int size;
    private final Token[][] grid;

    public Board(){
        this(10);
    }
    public Board(int size) {
        this.size = size;
        this.grid = new Token[size][size];
        clean();
    }

    public void clean() {
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[col].length; row++) {
                grid[col][row] = new Token("・");
            }
        }
    }

    public void placeToken(int col, int row, Token token) {
        if (col >= 0 && col < size && row >= 0 && row < size) {
            grid[col][row] = token;
        }
    }

    public Token square(int col, int row) {
        if (col >= 0 && col < size && row >= 0 && row < size) {
            return grid[col][row];
        }
        return null;
    }


    public void display() {
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[col].length; row++) {
                System.out.print(grid[col][row].label + " ");
            }
            System.out.println();
        }
    }
}
