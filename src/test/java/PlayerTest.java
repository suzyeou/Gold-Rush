import edu.io.Board;
import edu.io.player.Player;
import edu.io.player.VitalsValues;
import edu.io.token.*;
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

    @Test
    void player_init_hydration_is_100() {
        Assertions.assertEquals(100, player.vitals.hydration());
    }

    @Test
    void move_dehydrates() {
        var h = player.vitals.hydration();
        player.interactWithToken(new EmptyToken());
        Assertions.assertEquals(
                h - VitalsValues.DEHYDRATION_MOVE,
                player.vitals.hydration());
    }

    @Test
    void collecting_gold_dehydrates() {
        var h = player.vitals.hydration();
        player.interactWithToken(new GoldToken());
        Assertions.assertEquals(
                h - VitalsValues.DEHYDRATION_GOLD,
                player.vitals.hydration());
        h = player.vitals.hydration();
        player.interactWithToken(new PyriteToken());
        Assertions.assertEquals(
                h - VitalsValues.DEHYDRATION_GOLD,
                player.vitals.hydration());
    }

    @Test
    void repairing_with_anvil_dehydrates() {
        var h = player.vitals.hydration();
        player.interactWithToken(new PickaxeToken());
        player.interactWithToken(new AnvilToken());
        Assertions.assertEquals(
                h - VitalsValues.DEHYDRATION_ANVIL,
                player.vitals.hydration());
    }

    @Test
    void interactWithToken_throws_when_player_is_dead() {
        player.vitals.dehydrate(player.vitals.hydration());
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> player.interactWithToken(new EmptyToken()));
    }

    @Test
    void water_token_gain_hydration() {
        player.vitals.hydrate(100);
        player.vitals.dehydrate(30);
        player.interactWithToken(new WaterToken(20));
        Assertions.assertEquals(100-30+20, player.vitals.hydration());
    }

    @Test
    void throws_on_null_in_assignToken() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> player.assignToken(null));
    }

    @Test
    void throws_on_null_in_interactWithToken() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> player.interactWithToken(null));
    }

}
