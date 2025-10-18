package edu.io.token;

import edu.io.Board;

public class PlayerToken extends Token {
    private Board board;
    private int row;
    private int col;

    public enum Move {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public PlayerToken(Board board) {
        super(Label.PLAYER_TOKEN_LABEL);
        this.board = board;
        board.placeToken(col, row, this);
    }

    public PlayerToken(Board board, int row, int col) {
        super(Label.PLAYER_TOKEN_LABEL);
        this.board = board;
        this.row = row;
        this.col = col;
    }

    public void move(Move dir) {
        int tempRow = row;
        int tempCol = col;

        switch (dir) {
            case UP: tempRow -= 1; break;
            case DOWN: tempRow += 1; break;
            case LEFT: tempCol -= 1; break;
            case RIGHT: tempCol += 1; break;
            case NONE: return;
            default: System.out.println("Invalid move direction");
        }

        if (tempRow < 0 || tempRow >= board.size ||  tempCol < 0 || tempCol >= board.size ) {
            throw new IllegalArgumentException("Cannot move outside the board");
        }

        board.placeToken(col, row, new EmptyToken());
        row = tempRow;
        col = tempCol;
        board.placeToken(col, row, this);
    }

    public Board.Coords pos(){
        return new Board.Coords(row, col);
    }
}
