package edu.io;

import edu.io.player.Player;
import edu.io.token.PickaxeToken;

public class Main {
    public static void main(String[] args) {
        System.out.println("Gold Rush");
        Game game = new Game();
        Player player = new Player();
        game.join(player);
        var x = new PickaxeToken();
        System.out.println(x.label());
        game.start();
    }
}
