import edu.io.player.NoTool;
import edu.io.player.Shed;
import edu.io.token.PickaxeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShedTest {

    private Shed shed;

    @BeforeEach
    void setUp() {
        this.shed = new Shed();
    }

    @Test
    void new_shed_is_empty() {
        Assertions.assertTrue(shed.isEmpty());
        Assertions.assertInstanceOf(NoTool.class, shed.getTool());
    }

    @Test
    void can_add_tool() {
        var tool = new PickaxeToken();
        shed.add(tool);
        Assertions.assertEquals(tool, shed.getTool());
    }

    @Test
    void throws_if_add_null() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> shed.add(null));
    }

    @Test
    void can_drop_tool() {
        shed.add(new PickaxeToken());
        shed.dropTool();
        Assertions.assertInstanceOf(NoTool.class, shed.getTool());
    }
}
