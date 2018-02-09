package com.carrdinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Peer2Peer implements DataSender, DataListener {

    public Peer peer; // Holds peers IP and port number
    public int port = 8008; // Port to host connection on
    public PeerConnection connection; // Socket over which communication takes place
    public DataInputStream inputStream; // Get info coming in over the connection
    public DataOutputStream outputStream; // Send info out over the connection
    public Thread thread;
    private boolean running;

    public Peer2Peer(){

    }

    // Background thread that processes messages
    public void start(){
        this.thread = new Thread(new Runnable() {
            public void run() {

            }
        });
        synchronized (this){
            running = true;
        }
        this.thread.setName("P2P: " + connection.toString());
    }

    public void stop(){

    }

    public void listen(){

    }

    private void setupOutputStream(){

    }

    private void setupInputStream(){

    }

    private void becomeHost(){

    }

    private void becomeClient() throws UnknownHostException, IOException {

    }

    public void send(){

    }


}
