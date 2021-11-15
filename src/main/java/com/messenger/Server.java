package com.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
    String name;
    int id;
    int port;
    ServerSocket serverSocket;
    BlockingQueue<String> messages;
    Map<Integer, Client> clientMap;

    Thread thread;
    Runnable routine = () -> {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                int id = Network.getInt(in);
                if (clientMap.get(id) != null) {
                    socket.close();
                    messages.add("[System] Already connected");
                    continue;
                }
                Network.sendInt(out, this.id);
                String name = Network.getString(in);
                Network.sendString(out, this.name);
                int port = Network.getInt(in);
                Network.sendInt(out, this.port);
                int n = Network.getInt(in);
                List<String> list = new LinkedList<>();
                for (int i = 0; i < n; i++) {
                    int j = Network.getInt(in);
                    String s = Network.getString(in);
                    if (clientMap.get(j) == null) {
                        list.add(s);
                    }
                }
                Network.sendInt(out, this.clientMap.size());
                for (var entry : this.clientMap.entrySet()) {
                    Network.sendInt(out, entry.getKey());
                    Network.sendString(out, entry.getValue().ip);
                }
                Client client = new Client(name, id, port, socket, this);
                clientMap.put(id, client);
                messages.add("[System] " + client.ip + " has connected");
                for (String s : list) {
                    connect(s);
                }
            }
        } catch (IOException e) {
            messages.add("[System] Failed incoming connection");
        }
    };

    void connect(String ip) {
        try {
            String[] addr = ip.split(":");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(addr[0], Integer.parseInt(addr[1])), 5000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Network.sendInt(out, this.id);
            int id = Network.getInt(in);
            if (clientMap.get(id) != null) {
                socket.close();
                messages.add("[System] Already connected");
                return;
            }
            Network.sendString(out, this.name);
            String name = Network.getString(in);
            Network.sendInt(out, this.port);
            int port = Network.getInt(in);
            Network.sendInt(out, this.clientMap.size());
            for (var entry : this.clientMap.entrySet()) {
                Network.sendInt(out, entry.getKey());
                Network.sendString(out, entry.getValue().ip);
            }
            int n = Network.getInt(in);
            List<String> list = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                int j = Network.getInt(in);
                String s = Network.getString(in);
                if (clientMap.get(j) == null) {
                    list.add(s);
                }
            }
            Client client = new Client(name, id, port, socket, this);
            clientMap.put(id, client);
            messages.add("[System] Connected to " + client.ip);
            for (String s : list) {
                connect(s);
            }
        } catch (IOException | IllegalArgumentException e) {
            messages.add("[System] Couldn't connect");
        }
    }

    void register(String port, String name) {
        try {
            if (!name.isBlank()) {
                this.name = name;
                messages.add("[System] Name set");
            } else {
                messages.add("[System] Blank name not allowed");
                throw new IllegalArgumentException();
            }
            serverSocket = new ServerSocket(Integer.parseInt(port));
            this.port = serverSocket.getLocalPort();
            thread.start();
            messages.add("[System] Listening on port " + this.port);
        } catch (IOException | IllegalArgumentException e) {
            messages.add("[System] Couldn't initialize server");
        }
    }

    void broadcastMessage(String message) {
        try {
            for (Client client : clientMap.values()) {
                Network.sendByte(client.out, (byte) 0);
                Network.sendString(client.out, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void input(String s) {
        if (!s.startsWith("!")) {
            messages.add(name + ": " + s);
            broadcastMessage(s);
        } else {
            messages.add("[Command] " + s);
            String[] l = s.split(" ");
            try {
                switch (l[0]) {
                    case "!register" -> register(l[1], l[2]);
                    case "!connect" -> connect(l[1]);
                    default -> messages.add("[System] Unknown command");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                messages.add("[System] Missing parameter");
            }
        }
    }

    Server() {
        name = "unnamed";
        id = ThreadLocalRandom.current().nextInt();
        messages = new LinkedBlockingQueue<>();
        clientMap = new HashMap<>();
        thread = new Thread(routine);
    }

    void terminate() {
        try {
            thread.interrupt();
            for (var entry : clientMap.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().terminate();
                    clientMap.remove(entry.getKey());
                }
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
