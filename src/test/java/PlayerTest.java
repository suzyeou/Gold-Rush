import edu.io.Board;
import edu.io.Player;
import edu.io.token.GoldToken;
import edu.io.token.PlayerToken;
import edu.io.token.Token;
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
        var gold = player.gold();
        player.interactWithToken(goldToken);
        Assertions.assertEquals(gold + 2.0, player.gold());
    }

    @Test
    void player_can_gain_gold() {
        var gold = player.gold();
        player.gainGold(1.0);
        Assertions.assertEquals(gold + 1.0, player.gold());
    }

    @Test
    void player_can_lose_gold() {
        player.gainGold(2.0);
        var gold = player.gold();
        player.loseGold(1.0);
        Assertions.assertEquals(gold - 1.0, player.gold());
    }

    @Test
    void gold_cannot_go_below_zero() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> player.loseGold(1.0));
        Assertions.assertEquals(0.0, player.gold());
    }

    @Test
    void gain_and_lose_amount_cannot_be_negative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> player.gainGold(-1.0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> player.loseGold(-1.0));
    }

}
