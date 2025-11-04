package edu.io.token;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;

public class PickaxeToken extends Token implements Tool, Repairable {
    private double gainFactor;
    private Token pickaxeToken = new EmptyToken();
    private int durability;
    private Token withToken;

    public PickaxeToken() {
        super(Label.PICKAXE_TOKEN_LABEL);
        this.gainFactor = 1.5;
        this.durability = 3;
    }

    public PickaxeToken(double gainFactor) {
        this();
        if (gainFactor > 0.0) {
            this.gainFactor = gainFactor;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public PickaxeToken(double gainFactor, int durability) {
        this(gainFactor);
        if (durability > 0.0) {
            this.durability = durability;
        }  else {
            throw new IllegalArgumentException();
        }
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

    public void use() {
        if (durability > 0) {
            this.durability--;
        }
    }

    public PickaxeToken useWith(Token token) {
        this.withToken = token;
        return this;
    }

    public PickaxeToken ifWorking(Runnable action) {
        if (!isBroken() && withToken instanceof GoldToken) {
            action.run();
            use();
        }
        return this;
    }

    public PickaxeToken ifBroken(Runnable action) {
        if (isBroken() && withToken instanceof GoldToken) {
            action.run();
        }
        return this;
    }

    public PickaxeToken ifIdle(Runnable action) {
        return this;
    }

    public void repair() {
        this.durability = 2;
    }
}
