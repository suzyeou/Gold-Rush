package edu.io.token;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;

public class PickaxeToken extends Token implements Tool, Repairable {
    private double gainFactor;
    private Token pickaxeToken = new EmptyToken();
    private int durability;
    private int maxDurability;
    private Token withToken;

    public PickaxeToken() {
        super(Label.PICKAXE_TOKEN_LABEL);
        gainFactor = 1.5;
        durability = 3;
        maxDurability = 3;
    }

    public PickaxeToken(double gainFactor) {
        this();
        if (gainFactor > 0.0) {
            this.gainFactor = gainFactor;
        } else {
            throw new IllegalArgumentException("Gain factor must be greater than 0.");
        }
    }

    public PickaxeToken(double gainFactor, int durability) {
        this(gainFactor);
        if (durability > 0) {
            this.durability = durability;
            this.maxDurability = durability;
        }  else {
            throw new IllegalArgumentException("Durability must be greater than 0.");
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
        if (isBroken()) {
            action.run();
        }
        return this;
    }

    public PickaxeToken ifIdle(Runnable action) {
        return this;
    }

    public void repair() {
        this.durability = maxDurability;
    }
}
