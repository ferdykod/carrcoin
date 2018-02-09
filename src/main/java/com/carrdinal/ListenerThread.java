package com.carrdinal;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {

    private ServerSocket serverSocket;
    private int port;
    private DataListener server;

    public ListenerThread(int port, DataListener server) {
        this.port = port;
        this.server = server;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        Socket clientSocket;
        try {
            // Keep accepting connections
            while ((clientSocket = serverSocket.accept()) != null) {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                while (dataInputStream.available() > 0){
                    String data = dataInputStream.readUTF();
                }

            }
        } catch (IOException e) {
            System.out.println("Listener failed..");
            throw new RuntimeException(e);
        }
    }
}
