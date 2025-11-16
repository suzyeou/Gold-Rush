import edu.io.token.WaterToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WaterTest {

    private WaterToken waterToken;

    @BeforeEach
    public void setUp() {
        waterToken = new WaterToken();
    }

    @Test
    void default_water_token_amount_is_10() {
        Assertions.assertEquals(10, waterToken.amount());
    }

    @Test
    void amount_can_be_in_range_0_100() {
        Assertions.assertEquals(21, new WaterToken(21).amount());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new WaterToken(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new WaterToken(101));
    }
}
