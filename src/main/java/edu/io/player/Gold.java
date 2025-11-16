package edu.io.player;

public class Gold {
    private double amount;

    public Gold() {}

    public Gold(double amount) {
        if (amount >= 0) {
            this.amount = amount;
        }  else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public double amount() {
        return amount;
    }

    public void gain(double amount) {
        if (amount >= 0) {
            this.amount += amount;
        } else {
            throw new IllegalArgumentException("Gold amount must be greater than 0.");
        }
    }

    public void lose(double amount) {
        if (amount >= 0) {
            if (this.amount - amount >= 0) {
                this.amount -= amount;
            } else {
                throw new IllegalArgumentException("Gold amount must be greater than 0.");
            }
        } else {
            throw new IllegalArgumentException("Gold amount cannot go below zero");
        }
    }
}
