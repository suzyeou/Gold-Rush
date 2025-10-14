import edu.io.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenTest {
    @Test
    void token_has_label() {
        Token token = new Token("@");
        Assertions.assertEquals("@", token.label);
    }

    @Test
    void token_has_only_one_ctor() {
        Assertions.assertEquals(1, Token.class.getConstructors().length);
    }
}
