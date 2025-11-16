import edu.io.Board;
import edu.io.Game;
import edu.io.player.Player;
import edu.io.token.PlayerToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class GameTest {

    Game game;
    Player player;

    @BeforeEach
    void setUp() {
        game = new Game();
        player = new Player();
    }

    @Test
    void player_join_game_and_get_token() {
        game.join(player);
        Assertions.assertInstanceOf(PlayerToken.class, player.token());
    }

    @Test
    void new_player_token_is_on_the_board() {
        game.join(player);
        try {
            // Find the field in the Game class that holds the board.
            // Never do this in real-life tests!
            // This is for educational purposes only.
            Field[] fields = Game.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(Board.class)) {
                    field.setAccessible(true);
                    Board board = (Board) field.get(game);
                    PlayerToken token = player.token();
                    Assertions.assertEquals(
                            token,
                            board.peekToken(
                                    token.pos().col(),
                                    token.pos().row()));
                }
            }
        }
        catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void has_method_start() {
        Assertions.assertDoesNotThrow(() -> Game.class.getMethod("start"));
    }

    @Test
    void throws_when_join_null() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> game.join(null));
    }
}
