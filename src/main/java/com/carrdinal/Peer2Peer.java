package com.carrdinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Peer2Peer implements DataSender, DataListener {

    public Peer peer; // Holds peers IP and port number
    public int port = 8008; // Port to host connection on
    public Socket connection; // Socket over which communication takes place
    public DataInputStream inputStream; // Get info coming in over the connection
    public DataOutputStream outputStream; // Send info out over the connection
    public ListenerThread peerThread; // Thread to listen on
    public Thread sendThread; // Thread to send on

    public Peer2Peer(){

    }

    public void start(){

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
