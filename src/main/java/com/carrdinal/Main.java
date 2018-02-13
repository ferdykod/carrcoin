package com.carrdinal;

import com.carrdinal.network.Peer2Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class Main {

    private static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        // Setup Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        int port = DEFAULT_PORT;
        if(args.length < 1){
            System.out.println("Setting port to default: " + DEFAULT_PORT);
        } else {
            port = Integer.parseInt(args[0]);
        }

        Peer2Peer peer = new Peer2Peer(port);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        String command;
        while(running){
            System.out.print(">>>");
            command = br.readLine();
            switch(command){
                case "q":
                case "quit":
                    System.out.println("Quitting...");
                    running = false;
                    break;
                case "send":
                    System.out.println("Sending!");
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }

}
