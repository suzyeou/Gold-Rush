package edu.io.net;

import edu.io.net.command.GetInfo;
import edu.io.net.command.Handshake;
import edu.io.net.command.JoinGame;
import edu.io.net.command.LeaveGame;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var gsc = new GameServerConnector("tcp://localhost:1313", new SocketConnector());
        gsc.connect();
        if (!gsc.isConnected()) {
            System.err.println("Could not connect to server");
            return;
        }

        gsc.issueCommand(new Handshake.Cmd("1.1.3"), res -> {
            System.out.println(res);
        });

        var in = new Scanner(System.in);
        System.out.print("Enter your name: ");
        var name = in.nextLine();

        gsc.issueCommand(new JoinGame.Cmd(name), res -> {
            System.out.println(res);
        });

        gsc.issueCommand(new GetInfo.Cmd(), res -> {
            System.out.println(res);
        });

        gsc.issueCommand(new LeaveGame.Cmd(), res -> {
            System.out.println(res);
        });

        new Scanner(System.in).nextLine();
    }


    //hello
}