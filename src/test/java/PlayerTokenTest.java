import edu.io.Board;
import edu.io.player.Player;
import edu.io.token.EmptyToken;
import edu.io.token.PlayerToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTokenTest {
    Board board;
    PlayerToken playerToken;
    Player player;

    @BeforeEach
    void setUp() {
        board = new Board();
        player = new Player();
        playerToken = new PlayerToken(player, board);
    }

    @Test
    void new_PlayerToken_is_placed_on_the_board() {
        Board.Coords pos = playerToken.pos();
        Assertions.assertEquals(playerToken, board.peekToken(pos.col(), pos.row()));
    }

    @Test
    void stay_inside_and_throws_when_go_too_far_left() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            while (true) playerToken.move(PlayerToken.Move.LEFT);
        });
        Board.Coords pos = playerToken.pos();
        Assertions.assertEquals(0, pos.col());
    }
    @Test
    void stay_inside_and_throws_when_go_too_far_right() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            while (true) playerToken.move(PlayerToken.Move.RIGHT);
        });
        Board.Coords pos = playerToken.pos();
        Assertions.assertEquals(board.size()-1, pos.col());
    }
    @Test
    void stay_inside_throws_when_go_too_far_up() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            while (true) playerToken.move(PlayerToken.Move.UP);
        });
        Board.Coords pos = playerToken.pos();
        Assertions.assertEquals(0, pos.row());
    }
    @Test
    void stay_inside_throws_when_go_too_far_down() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            while (true) playerToken.move(PlayerToken.Move.DOWN);
        });
        Board.Coords pos = playerToken.pos();
        Assertions.assertEquals(board.size()-1, pos.row());
    }

    @Test
    void move_moves_token() {
        Board.Coords pos = playerToken.pos();
        try {
            playerToken.move(PlayerToken.Move.DOWN);
            Assertions.assertEquals(
                    playerToken,
                    board.peekToken(pos.col(), pos.row() + 1));
        }
        catch (IllegalArgumentException e) {
            playerToken.move(PlayerToken.Move.UP);
            Assertions.assertEquals(
                    playerToken,
                    board.peekToken(pos.col(), pos.row() - 1));
        }
    }

    @Test
    void after_move_prev_square_is_empty() {
        Board.Coords pos = playerToken.pos();
        playerToken.move(PlayerToken.Move.RIGHT);
        Assertions.assertInstanceOf(
                EmptyToken.class,
                board.peekToken(pos.col(), pos.row()));
    }

    @Test
    void throws_on_null_in_ctor() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {new PlayerToken(null, board);});
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {new PlayerToken(player, null);});
    }

    @Test
    void throws_on_null_in_move() {
        Assertions.assertThrows(NullPointerException.class,
                () -> playerToken.move(null));
    }
}
