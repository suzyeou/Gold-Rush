package edu.io.player;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Vitals {
    private int hydration;
    private Runnable onDeathCallback;

    public Vitals() {
        hydration = 100;
        onDeathCallback = () -> {};
    }

    public int hydration() {
        return hydration;
    }

    public void hydrate(int amount) {
        if (amount >= 0) {
            hydration += amount;
            if (hydration > 100) {
                hydration = 100;
            }
        } else  {
            throw new IllegalArgumentException("Hydration cannot be negative");
        }
    }

    public void dehydrate(int amount) {
        if (amount >= 0) {
            hydration -= amount;
            if (hydration <= 0) {
                hydration = 0;
                onDeathCallback.run();
            }
        } else {
            throw new IllegalArgumentException("Dehydration cannot be negative");
        }
    }

    public void setOnDeathHandler(@NotNull Runnable callback) {
        onDeathCallback = Objects.requireNonNull(callback, "Callback cannot be null");
    }

    public boolean isAlive() {
        return hydration > 0;
    }
}
