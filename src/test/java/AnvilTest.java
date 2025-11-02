import edu.io.player.Player;
import edu.io.token.AnvilToken;
import edu.io.token.GoldToken;
import edu.io.token.PickaxeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnvilTest {

    @Test
    void anvil_repairs_pickaxe() {
        var DUR = 2;
        var player = new Player();
        var pickaxeToken = new PickaxeToken(1.5, DUR);
        player.interactWithToken(pickaxeToken);

        pickaxeToken.useWith(new GoldToken()).ifWorking(() -> {});
        Assertions.assertEquals(DUR-1, pickaxeToken.durability());

        player.interactWithToken(new AnvilToken());
        Assertions.assertEquals(DUR, pickaxeToken.durability());
    }
}
