import edu.io.player.Vitals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VitalsTest {

    private Vitals vitals;
    private boolean testb;

    @BeforeEach
    void setUp() {
        vitals = new Vitals();
    }

    @Test
    void default_hydration_is_100() {
        Assertions.assertEquals(100, vitals.hydration());
    }

    @Test
    void can_dehydrate_and_hydrate() {
        var h = vitals.hydration();
        vitals.dehydrate(20);
        Assertions.assertEquals(h - 20, vitals.hydration());
        vitals.hydrate(20);
        Assertions.assertEquals(h, vitals.hydration());
    }

    @Test
    void throw_if_amount_param_is_negative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> vitals.hydrate(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> vitals.dehydrate(-1));
    }

    @Test
    void min_hydration_amount_is_0() {
        var h = vitals.hydration();
        vitals.dehydrate(h);
        Assertions.assertEquals(0, vitals.hydration());
        vitals.dehydrate(h);
        Assertions.assertEquals(0, vitals.hydration());
    }

    @Test
    void max_hydration_amount_is_100() {
        vitals.hydrate(100);
        Assertions.assertEquals(100, vitals.hydration());
        vitals.hydrate(100);
        Assertions.assertEquals(100, vitals.hydration());
    }

    @Test
    void isAlive_is_true_when_hydration_is_not_0() {
        vitals.hydrate(10);
        Assertions.assertTrue(vitals.isAlive());
        vitals.dehydrate(100);
        Assertions.assertFalse(vitals.isAlive());
    }

    @Test
    void can_set_callback_onDeath() {
        testb = false;
        vitals.setOnDeathHandler(() -> testb = true);
        vitals.dehydrate(vitals.hydration());
        Assertions.assertTrue(testb);
    }

    @Test
    void throws_when_callback_is_null() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> vitals.setOnDeathHandler(null));
    }
}
