import edu.io.player.Gold;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GoldTest {

    private Gold gold;

    @BeforeEach
    void setUp() {
        this.gold = new Gold();
    }

    @Test
    void default_amount_is_0() {
        Assertions.assertEquals(0.0, gold.amount());
    }

    @Test
    void can_init_gold_with_amount() {
        var gold = new Gold(3.0);
        Assertions.assertEquals(3.0, gold.amount());
    }

    @Test
    void throws_when_creating_gold_with_neg_amount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> { new Gold(-1.0); });
    }

    @Test
    void player_can_gain_gold() {
        var a = gold.amount();
        gold.gain(1.0);
        Assertions.assertEquals(a + 1.0, gold.amount());
    }

    @Test
    void player_can_lose_gold() {
        gold.gain(2.0);
        var a = gold.amount();
        gold.lose(1.0);
        Assertions.assertEquals(a - 1.0, gold.amount());
    }

    @Test
    void gold_cannot_go_below_zero() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gold.lose(1.0));
        Assertions.assertEquals(0.0, gold.amount());
    }

    @Test
    void gain_and_lose_amount_cannot_be_negative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gold.gain(-1.0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gold.lose(-1.0));
    }
}
