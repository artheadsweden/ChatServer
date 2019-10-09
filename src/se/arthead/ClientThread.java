package se.arthead;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ClientThread extends Thread {
    private Socket clientSocket;
    private BlockingQueue<Message> queue;
    private Map<String, Socket> connected;

    public ClientThread(Socket clientSocket, BlockingQueue<Message> queue, Map<String, Socket> connected) {
        this.clientSocket = clientSocket;
        this.queue = queue;
        this.connected = connected;
    }

    public void run() {
        try {
            System.out.println("Got connection from " + clientSocket.getInetAddress());
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            //Logga in
            String username = dataInputStream.readUTF();
            System.out.println(username + " signed in");
            connected.put(username, clientSocket);
            while(clientSocket.isConnected()) {
                // OBS Hur hanterar vi ett ev. datarace för en socket?
                String inputData = dataInputStream.readUTF();
                Message message = new Message(username, inputData);
                queue.put(message);
            }
            Message message = new Message("System", username + " has left the chat");
            queue.put(message);
            connected.remove(username);
            System.out.println(username + " disconnected...");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
