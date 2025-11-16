package edu.io;

import edu.io.token.EmptyToken;
import edu.io.token.Token;

import java.util.Objects;

public class Board {
    public final int size;
    private final Token[][] grid;

    public Board(){
        this(10);
        clean();
    }

    public Board(int size) {
        this.size = size;
        this.grid = new Token[size][size];
        clean();
    }

    public record Coords(int row, int col) {}

    public int size(){
        return size;
    }

    public void clean() {
        Token t = new EmptyToken();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = t;
            }
        }
    }

    public void placeToken(int col, int row, Token token) {
        Objects.requireNonNull(token, "Token cannot be null");
        if (col >= 0 && col < size && row >= 0 && row < size) {
            grid[row][col] = token;
        }
    }

    public Token peekToken(int col, int row) {
        if (col >= 0 && col < size && row >= 0 && row < size) {
            return grid[row][col];
        }
        return null;
    }

    public void display() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                System.out.print(grid[row][col].label() + " ");
            }
            System.out.println();
        }
    }

    public Coords getAvailableSquare(){
        Token t = new EmptyToken();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] instanceof EmptyToken) {
                    return new Coords(row, col);
                }
            }
        }
       throw  new IllegalStateException("No available square");
    }
}
