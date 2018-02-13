package com.carrdinal.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Peer2Peer {

    private int port = 8888;
    private HashSet<Peer>    peers;
    private DataInputStream  inputStream;
    private DataOutputStream outputStream;
    private Thread           serverThread;
    private Thread           clientThread;
    private boolean          runningServer;

    public Peer2Peer(int port){
        this.port = port;
        peers = new HashSet<>();
        serverThread = new Thread(new Runnable() {
            public void run() {
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start(){
        if(serverThread.isAlive()){
            System.out.println("Server is already running.");
            return;
        }
        runningServer = true;
        serverThread.start();
    }

    public void stop(){
        runningServer = false;
    }

    public void listen() throws IOException{
        System.out.println("Server starting...");
        ServerSocket server = new ServerSocket(this.port);
        System.out.println("Server started on port " + this.port);

        String command;
        Peer peer;
        while(runningServer){
            Socket socket = server.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String clientAddress = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort();
            System.out.println("Connection received from: " + clientAddress + ":" + clientPort);
            peer = new Peer(socket.getInetAddress().getHostAddress(), clientPort);
            peers.add(peer);

            System.out.println("New peer: " + peer.toString());
            command = receive(in);
            send(serve(command), out);
        }
    }

    public void connect(String host, int port){
        try {
            Socket socket = new Socket(host, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serve(String command) {
        switch (command) {
            case "ping":
                return "pong";
            default:
                return "command unknown";
        }
    }

    public void send(String data, DataOutputStream out){
        System.out.println("Sending message: " + data);
        try {
            out.writeUTF(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive(DataInputStream in){
        String data = null;
        try {
            data = in.readUTF();
            System.out.println("Received message: "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) throws IOException {

    }


}
