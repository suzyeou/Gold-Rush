package edu.io.token;

public class GoldToken extends Token {
    private double amount;

    public GoldToken() {
        super(Label.GOLD_TOKEN_LABEL);
        this.amount = 1.0;
    }

    public GoldToken(double amount) {
        super(Label.GOLD_TOKEN_LABEL);
        if (amount >= 0.0) {
            this.amount = amount;
        }  else {
            throw new IllegalArgumentException("Gold amount must be greater than 0.");
        }
    }

    public double amount() {
        return amount;
    }
}
