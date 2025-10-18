import edu.io.Board;
import edu.io.token.GoldToken;
import edu.io.token.Label;
import edu.io.token.PlayerToken;
import edu.io.token.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void new_board_is_clean() {
        Assertions.assertTrue(_is_board_clean());
    }

    @Test
    void can_place_token() {
        Token t = new PlayerToken(board);
        board.placeToken(1, 2, t);
        Assertions.assertEquals(t, board.peekToken(1, 2));
    }

    @Test
    void clean_method_works() {
        board.placeToken(1, 2, new GoldToken());
        board.placeToken(board.size()-1, board.size()-1,
                new GoldToken());
        board.clean();
        Assertions.assertTrue(_is_board_clean());
    }

    @Test
    void display_method_exists() {
        Assertions.assertDoesNotThrow(() -> Board.class.getMethod("display"));
    }

    // -- utils
    boolean _is_board_clean() {
        int size = board.size();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!board.peekToken(col, row).label()
                        .equals(Label.EMPTY_TOKEN_LABEL)) {
                    return false;
                }
            }
        }
        return true;
    }
}
