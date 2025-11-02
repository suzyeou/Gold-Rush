package edu.io.interfaces;

import edu.io.token.Token;

public interface Tool {
    public Tool useWith(Token token);
    public Tool ifWorking(Runnable action);
    public Tool ifBroken(Runnable action);
    public Tool ifIdle(Runnable action);
    public boolean isBroken();
}
