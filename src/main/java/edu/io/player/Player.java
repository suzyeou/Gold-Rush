package edu.io.player;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;
import edu.io.token.*;
import java.util.Objects;


public class Player {
    private PlayerToken token;
    public final Gold gold = new Gold();
    public final Shed shed = new Shed();
    public final Vitals vitals = new Vitals();

    public PlayerToken token() {
        return token;
    }

    public void assignToken(PlayerToken token) {
        this.token = Objects.requireNonNull(token, "Token cannot be null");
    }

    public void interactWithToken(Token token) {
        Objects.requireNonNull(token, "Token cannot be null");

        if (!vitals.isAlive()) {
            throw new IllegalStateException("Player is dead");
        }

        if (token instanceof GoldToken goldToken) {
            Tool tool = shed.getTool();
            vitals.dehydrate(VitalsValues.DEHYDRATION_GOLD);

            if (tool instanceof PickaxeToken pickaxeToken) {
                pickaxeToken.useWith(goldToken)
                        .ifWorking(() -> {
                            gold.gain(goldToken.amount() * pickaxeToken.gainFactor());
                        })
                        .ifBroken(() -> {
                            gold.gain(goldToken.amount());
                            shed.dropTool();
                        })
                        .ifIdle(() -> {
                            gold.gain(goldToken.amount());
                        });
            } else {
                gold.gain(goldToken.amount());
            }
        }
        else if (token instanceof PickaxeToken pickaxeToken) {
            shed.add(pickaxeToken);
        }
        else if (token instanceof AnvilToken anvilToken) {
            vitals.dehydrate(VitalsValues.DEHYDRATION_ANVIL);
            if (shed.getTool() instanceof Repairable tool) {
                tool.repair();
            }
        }
        else if (token instanceof EmptyToken emptyToken) {
            vitals.dehydrate(VitalsValues.DEHYDRATION_MOVE);
        }
        else if (token instanceof WaterToken waterToken) {
            vitals.hydrate(waterToken.amount());
        }
    }
}