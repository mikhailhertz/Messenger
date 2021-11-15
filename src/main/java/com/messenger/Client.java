package com.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client {
    String name;
    int id;
    String ip;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    Server server;

    Thread thread;
    Runnable routine = () -> {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte type = Network.getByte(in);
                String message = Network.getString(in);
                server.messages.add(name + ": " + message);
            }
        } catch (IOException e) {
            server.clientMap.remove(id);
            server.messages.add("[System] Client " + name + " has disconnected");
            Thread.currentThread().interrupt();
        }
    };

    Client(String name, int id, int port, Socket socket, Server server) throws IOException {
        this.name = name;
        this.id = id;
        this.ip = socket.getInetAddress().getHostAddress() + ":" + port;
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.server = server;
        thread = new Thread(routine);
        thread.start();
    }

    void terminate() {
        try {
            thread.interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
