package edu.io.player;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;
import edu.io.token.*;


public class Player {
    private PlayerToken token;
    public Gold gold = new Gold();
    private Shed shed = new Shed();

    public PlayerToken token() {
        return token;
    }

    public void assignToken(PlayerToken token) {
        this.token = token;
    }

    public void interactWithToken(Token token) {
        if (token instanceof GoldToken goldToken) {
            Tool tool = shed.getTool();
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
        } else if (token instanceof PickaxeToken pickaxeToken) {
            shed.add(pickaxeToken);
        } else if (token instanceof AnvilToken anvilToken) {
            if (shed.getTool() instanceof Repairable tool) {
                tool.repair();
            }
        }
    }
}