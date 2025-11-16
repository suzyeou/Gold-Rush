package edu.io.player;

import edu.io.interfaces.Tool;
import java.util.Stack;


public class Shed {
    private Stack<Tool> tools;

    public Shed () {
        tools = new Stack<>();
    }

    public boolean isEmpty() {
        return tools.isEmpty();
    }

    public void add(Tool tool) {
        if (tool != null) {
            tools.push(tool);
        } else {
            throw new IllegalArgumentException();
        }
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
