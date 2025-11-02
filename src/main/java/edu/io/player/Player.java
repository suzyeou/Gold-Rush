package edu.io.player;

import edu.io.interfaces.Repairable;
import edu.io.interfaces.Tool;
import edu.io.token.*;

import java.util.Stack;

public class Player {
    private PlayerToken token;
    private boolean hasPickaxe;
    public final Gold gold = new Gold();
    public Shed shed = new Shed();

    public PlayerToken token() {
        return token;
    }

    public void assignToken(PlayerToken token) {
        this.token = token;
    }

    public void interactWithToken(Token token) {
        switch (token) {
            //błąd (use --enable-preview to enable patterns in switch statements), error: patterns in switch statements are a preview feature and are disabled by default.
            case GoldToken goldToken -> {
                Tool tool = shed.getTool();
                if (tool instanceof PickaxeToken) {
                    tool.useWith(goldToken)
                            .ifWorking(() -> {
                                gold.gain(gold.amount() * ((PickaxeToken) tool).gainFactor());
                            })
                            .ifBroken(() -> {
                                gold.gain(gold.amount());
                                shed.dropTool();
                            })
                            .ifIdle(() -> {
                                gold.gain(gold.amount());
                            });
                }
            }
            case PickaxeToken pickaxeToken -> {
                shed.add(pickaxeToken);
            }
            case AnvilToken anvilToken -> {
                if (shed.getTool() instanceof Repairable tool) {
                    tool.repair();
                }
            }
            default -> {}
        }
    }
}

class Shed {
    private Stack<Tool> tools;

    public Shed () {
        tools = new Stack<>();
    }

    public boolean isEmpty() {
        return tools.isEmpty();
    }

    public void add(Tool tool) {
        tools.push(tool);
    }

    public Tool getTool() {
        if (tools.isEmpty()) {
            return new NoTool();
        } else {
            return tools.peek();
        }
    }

    public void dropTool() {
        tools.pop();

    }
}
