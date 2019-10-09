package se.arthead;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Broadcast extends Thread {
    private BlockingQueue<Message> queue;
    private Map<String, Socket> connected;

    public Broadcast(BlockingQueue<Message> queue, Map<String, Socket> connected) {
        this.queue = queue;
        this.connected = connected;
    }

    public void run() {
        while(true) {
            try {
                Message message = queue.take();
                for(Map.Entry<String, Socket> entry : connected.entrySet()) {
                    if(!message.getSender().equals(entry.getKey())) {
                        // OBS Hur hanterar vi ett ev. datarace för en socket?
                        DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(entry.getValue().getOutputStream()));
                        dataOutput.writeUTF(message.getMessage());
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
