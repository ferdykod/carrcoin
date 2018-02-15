package com.carrdinal.network;

import com.carrdinal.network.commands.Command;
import com.carrdinal.network.commands.GetBlockCommandHandler;
import com.carrdinal.network.commands.GetBlockCountCommandHandler;
import com.carrdinal.network.commands.GetTransactionCommandHandler;
import com.carrdinal.network.commands.PingCommandHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Peer2Peer {

    private int port = 8888;
    private HashSet<Peer>    peers;
    private DataInputStream  inputStream;
    private DataOutputStream outputStream;
    private Thread           serverThread;
    private Thread           clientThread;
    private boolean          runningServer;
    private HashMap<String, Command> commands = new HashMap<>();

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

        initializeCommands();
    }

    private void initializeCommands() {
        this.commands.put("ping", new PingCommandHandler());
        this.commands.put("getblockcount", new GetBlockCountCommandHandler());
        this.commands.put("getblock", new GetBlockCommandHandler());
        this.commands.put("gettransaction", new GetTransactionCommandHandler());
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

    private String serve(String input) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) {
            list.add(m.group(1));
        }

        String command = list.remove(0); // Get the command and remove it from the list.

        if(!commands.containsKey(command)){
            return "'" + command + "' is not a command.";
        }

        String[] args = null;
        if (list.size() > 0){
            args = list.toArray(new String[list.size()]);
        }

        return commands.get(command).execute(args);
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
        Peer2Peer node1 = new Peer2Peer(8888);
        Peer2Peer node2 = new Peer2Peer(8889);

        node1.start();
        node2.connect("127.0.0.1", 8888);
        node2.send("ping", node2.outputStream);
    }


}
