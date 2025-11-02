import edu.io.token.GoldToken;
import edu.io.token.PyriteToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoldTokenTest {

    @Test
    void default_gold_amount_is_one() {
        var t = new GoldToken();
        Assertions.assertEquals(1.0, t.amount());
    }

    @Test
    void can_be_created_with_amount() {
        var t = new GoldToken(1.5);
        Assertions.assertEquals(1.5, t.amount());
    }

    @Test
    void throws_when_amount_is_negative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new GoldToken(-1));
    }

    @Test
    void pyrite_is_gold_with_no_value() {
        var t = new PyriteToken();
        Assertions.assertInstanceOf(GoldToken.class, t);
        Assertions.assertEquals(0.0, t.amount());
    }
}
