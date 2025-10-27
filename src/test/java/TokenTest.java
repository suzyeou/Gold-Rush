import edu.io.Board;
import edu.io.Player;
import edu.io.token.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Modifier;

class TokenTest {
    @Test
    void token_has_label() {
        Token token = new EmptyToken();
        Assertions.assertEquals(Label.EMPTY_TOKEN_LABEL, token.label());
    }

    @Test
    void token_has_only_one_ctor() {
        Assertions.assertEquals(1, Token.class.getConstructors().length);
    }

    @Test
    void test_EmptyToken_label() {
        Assertions.assertEquals(
                Label.EMPTY_TOKEN_LABEL,
                new EmptyToken().label());
    }

    @Test
    void test_GoldToken_label() {
        Assertions.assertEquals(
                Label.GOLD_TOKEN_LABEL,
                new GoldToken().label());
    }

    @Test
    void test_PlayerToken_label() {
        Assertions.assertEquals(
                Label.PLAYER_TOKEN_LABEL,
                new PlayerToken(new Player(), new Board()).label());
    }

    @Test
    void class_Token_is_abstract() {
        Assertions.assertTrue(Modifier.isAbstract(Token.class.getModifiers()));
    }
}
