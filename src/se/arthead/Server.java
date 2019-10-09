package se.arthead;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private static final int port = 9876;
    private BlockingQueue<Message> queue;
    private Map<String, Socket> connected;

    public Server() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Starting server");
            queue = new ArrayBlockingQueue<Message>(1024);
            connected = new ConcurrentHashMap<String, Socket>();
        } catch (IOException e) {
            System.out.println("Port " + port + " already in use!");
            e.printStackTrace();
        }
    }

    public void start() {
        Broadcast broadcast = new Broadcast(queue, connected);
        broadcast.start();
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread client = new ClientThread(clientSocket, queue, connected);
                client.start();
            } catch (IOException e) {
                System.out.println("Connection was aborted");
            }
        }
    }
}
