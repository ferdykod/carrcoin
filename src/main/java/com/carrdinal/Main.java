package com.carrdinal;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;
import com.carrdinal.network.Peer2Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        // Setup Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        Thread miningThread = null;

        final BlockChain blockchain = BlockChain.loadFromFile("blockchain.json");
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
            command = br.readLine();
            switch(command){
                case "q":
                case "quit":
                    System.out.println("Quitting...");
                    running = false;
                    break;
                case "save_blockchain":
                    System.out.println("Saving blockchain to file...");
                    blockchain.saveToFile();
                    System.out.println("Saved to file");
                    break;
                case "mine":
                    System.out.println("Mining a new block");
                    if(miningThread != null && miningThread.isAlive()){
                        System.out.println("Mining already!");
                    }
                    miningThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            blockchain.addBlock();
                        }
                    });
                    miningThread.start();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }

}
