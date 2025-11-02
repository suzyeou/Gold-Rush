import edu.io.Board;
import edu.io.player.Player;
import edu.io.token.GoldToken;
import edu.io.token.PlayerToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {
    Board  board;
    Player player;

    @BeforeEach
    void setUp() {
        board = new Board();
        player = new Player();
    }

    @Test
    void can_assign_token_to_player() {
        PlayerToken token = new PlayerToken(player, board);
        player.assignToken(token);
        Assertions.assertEquals(token, player.token());
    }

    @Test
    void player_can_interact_with_gold() {
        var goldToken = new GoldToken(2.0);
        var gold = player.gold.amount();
        player.interactWithToken(goldToken);
        Assertions.assertEquals(gold + 2.0, player.gold.amount());
    }

}
