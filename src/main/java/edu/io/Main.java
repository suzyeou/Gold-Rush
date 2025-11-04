package edu.io;

import edu.io.player.Player;

public class Main {
    public static void main(String[] args) {
        System.out.println("Gold Rush");
        Game game = new Game();
        Player player = new Player();
        game.join(player);
        game.start();
    }
}
