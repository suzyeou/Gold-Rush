package edu.io.token;

public class WaterToken extends Token {
    private int amount;

    public WaterToken() {
        super(Label.WATER_TOKEN_LABEL);
        amount = 10;
    }

    public WaterToken(int amount) {
        this();
        if (amount >= 0 && amount <= 100) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be between 0 and 100");
        }
    }

    public int amount() {
        return amount;
    }
}
