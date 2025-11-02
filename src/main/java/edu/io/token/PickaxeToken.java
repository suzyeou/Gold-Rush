package edu.io.token;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;

public class PickaxeToken extends Token implements Tool, Repairable {
    private double gainFactor = 1.5;
    private Token pickaxeToken = new EmptyToken();
    private int durability;
    private Token withToken;

    public PickaxeToken() {
        super(Label.PICKAXE_TOKEN_LABEL);
    }

    public PickaxeToken(double gainFactor) {
        super(Label.PICKAXE_TOKEN_LABEL);
        this.gainFactor = gainFactor;
    }

    public PickaxeToken(double gainFactor,int durability) {
        super(Label.PICKAXE_TOKEN_LABEL);
        this.gainFactor = gainFactor;
        this.durability = durability;
    }

    public double gainFactor() {
        return gainFactor;
    }

    public int durability() {
        return durability;
    }

    public boolean isBroken() {
        return durability <= 0;
    }

    public PickaxeToken useWith(Token token) {
        return this;
    }

    public PickaxeToken ifWorking(Runnable action) {
        if (isBroken()) action.run();
        return this;
    }

    public PickaxeToken ifBroken(Runnable action) {
        return this;
    }

    public PickaxeToken ifIdle(Runnable action) {
        return this;
    }

    public void repair() {}
}
