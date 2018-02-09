package com.carrdinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;

public class PeerConnection {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final VersionMessage versionMessage;

    // IP of the peer we're connecting to
    private final InetAddress remoteIP;
    private final int defaultPort = 8333;

    private final CarrcoinSerializer serializer = null;

    public PeerConnection(InetAddress remoteIP, int bestHeight, int connectionTimeout) throws IOException{
        this.remoteIP = remoteIP;
        InetSocketAddress address = new InetSocketAddress(remoteIP, defaultPort);
        socket = new Socket();
        socket.connect(address, connectionTimeout);

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        // Announce ourselves, send version message
        writeMessage(new VersionMessage("0.1", bestHeight));
        // Get peers version message
        versionMessage = (VersionMessage) readMessage();
        // Send an ACK message back to say we accept their version
        writeMessage(new VersionAckMessage());
        // Wait for their ACK
        readMessage();

        // TODO switch to peers version
        if(!versionMessage.hasBlockChain()){
            // TODO start sending blocks?
        }
    }

    public void ping() throws IOException {
        writeMessage(new PingMessage());
    }

    public void shutdown() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
    }

    @Override
    public String toString(){
        return "[" + remoteIP.getHostAddress() + "]:" + defaultPort + " (" + (socket.isConnected() ? "connected" : "disconnected") + ")";
    }

    public Message readMessage() throws IOException, ProtocolException {
        return serializer.deserialize(in);
    }

}
