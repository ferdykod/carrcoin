package com.carrdinal.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Peer2Peer {

    private int port = 8888;
    private Socket       clientSocket;
    private Socket       serverSocket;
    private ServerSocket server;
    private ArrayList<Peer> peers = new ArrayList<Peer>();
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Thread serverThread;
    private Thread clientThread;
    private boolean runningServer;

    public Peer2Peer(int port){
        this.port = port;
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
        this.server = new ServerSocket(this.port);
        System.out.println("Server started on port " + this.port);

        String command;
        Peer peer;
        while(runningServer){
            serverSocket = server.accept();
            inputStream = new DataInputStream(serverSocket.getInputStream());
            outputStream = new DataOutputStream(serverSocket.getOutputStream());
            System.out.println("Connection received from: " + serverSocket.toString());
            peer = new Peer(serverSocket.getInetAddress().toString(), serverSocket.getPort());
            peers.add(peer);

            System.out.println("New peer: " + peer.toString());
            command = receive();
            send(serve(command));
        }
    }

    public void connect(String host, int port){
        try {
            clientSocket = new Socket(host, port);
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());

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

    public void send(String data){
        System.out.println("Sending message: " + data);
        try {
            outputStream.writeUTF(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive(){
        String data = null;
        try {
            data = inputStream.readUTF();
            System.out.println("Received message: "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args){
        Peer2Peer peer1 = new Peer2Peer(8888);
        Peer2Peer peer2 = new Peer2Peer(8889);

        peer1.start();
        peer2.start();

        peer1.connect("127.0.0.1", 8889);
        peer2.connect("127.0.0.1", 8888);
        peer1.send("ping");
        peer1.receive();
    }


}
