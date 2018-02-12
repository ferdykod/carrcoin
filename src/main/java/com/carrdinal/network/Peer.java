package com.carrdinal.network;

public class Peer {

    public String address;
    public int    port;

    public Peer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("Peer [%s]:%s", address, port);
    }

}
