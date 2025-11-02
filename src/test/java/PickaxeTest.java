import edu.io.player.Player;
import edu.io.token.EmptyToken;
import edu.io.token.GoldToken;
import edu.io.token.PickaxeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PickaxeTest {

    @Test
    void pickaxe_gain_when_player_interacts_with_gold() {
        var player = new Player();
        player.interactWithToken(new PickaxeToken());
        player.interactWithToken(new GoldToken(2.0));
        Assertions.assertEquals(2.0 * 1.5, player.gold.amount());
    }


    @Test
    void default_gainFactor_is_1_5() {
        Assertions.assertEquals(1.5, new PickaxeToken().gainFactor());
    }

    @Test
    void can_create_pickaxe_with_custom_gainFactor() {
        Assertions.assertEquals(1.75, new PickaxeToken(1.75).gainFactor());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PickaxeToken(0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PickaxeToken(-1));
    }

    @Test
    void gainFactor_affects_collected_gold() {
        var player = new Player();
        player.interactWithToken(new PickaxeToken(1.75));
        player.interactWithToken(new GoldToken(2.0));
        Assertions.assertEquals(1.75 * 2.0, player.gold.amount());
    }

    @Test
    void default_durability_is_3() {
        Assertions.assertEquals(3, new PickaxeToken().durability());
    }

    @Test
    void can_create_pickaxe_with_custom_durability() {
        Assertions.assertEquals(5, new PickaxeToken(1.0, 5).durability());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PickaxeToken(1.0, 0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PickaxeToken(1.0, -1));
    }

    @Test
    void pickaxe_use_decrement_durability() {
        var n = 3;
        var t = new PickaxeToken(1.5, n);
        for (var i=0; i<n; i++) {
            Assertions.assertTrue(t.durability() > 0);
            t.use();
        }
        Assertions.assertEquals(0, t.durability());
    }

    @Test
    void broken_pickaxe_is_unusable() {
        // Arrange
        var player = new Player();
        var goldToken = new GoldToken(2.0);
        var pickaxeToken = new PickaxeToken(1.5, 1);
        player.interactWithToken(pickaxeToken);
        pickaxeToken.use();
        // Act
        player.interactWithToken(goldToken);
        // Assert
        Assertions.assertTrue(pickaxeToken.isBroken());
        Assertions.assertEquals(2.0, player.gold.amount());
    }

    @Test
    void can_use_pickaxe_with_gold() {
        new PickaxeToken().useWith(new GoldToken())
               .ifWorking(() -> {
                   Assertions.assertTrue(true);
               })
               .ifBroken(Assertions::fail)
               .ifIdle(Assertions::fail);
    }

    @Test
    void can_use_pickaxe_with_other_than_gold_but_no_effect() {
        new PickaxeToken().useWith(new EmptyToken())
                .ifWorking(Assertions::fail)
                .ifBroken(Assertions::fail)
                .ifIdle(() -> {
                    Assertions.assertTrue(true);
                });
    }

    @Test
    void broken_pickaxe_doesnt_work() {
        var pickaxeToken = new PickaxeToken(1.5, 1);
        pickaxeToken.useWith(new GoldToken()).ifWorking(()->{});
        pickaxeToken.useWith(new GoldToken())
                .ifWorking(Assertions::fail)
                .ifBroken(() -> {
                    Assertions.assertTrue(true);
                });
    }
}
