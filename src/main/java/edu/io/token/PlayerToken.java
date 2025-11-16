package edu.io.token;

import edu.io.Board;
import edu.io.Board.Coords;
import edu.io.player.Player;

import java.util.Objects;

public class PlayerToken extends Token {
    private Player player;
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

    public PlayerToken(Player player, Board board) {
        super(Label.PLAYER_TOKEN_LABEL);
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        Coords squareCoords = board.getAvailableSquare();
        row = squareCoords.row();
        col = squareCoords.col();
        board.placeToken(col, row, this);
    }

    public void move(Move dir) {
        Objects.requireNonNull(dir, "Move cannot be null");
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

        player.interactWithToken(board.peekToken(tempRow, tempCol));

        board.placeToken(col, row, new EmptyToken());
        row = tempRow;
        col = tempCol;
        board.placeToken(col, row, this);
    }

    public Coords pos(){
        return new Coords(row, col);
    }
}
