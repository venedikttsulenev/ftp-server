package app;

import concurrentUtils.Stoppable;
import netUtils.Message;

import java.io.DataInputStream;
import java.io.IOException;

public class ConnectionChecker implements Stoppable {
    private final DataInputStream dataInputStream;
    private final Client client;
    private boolean disconnected = false;
    private boolean alive = true;
    public ConnectionChecker(DataInputStream dataInputStream, Client client) {
        this.dataInputStream = dataInputStream;
        this.client = client;
    }
    public void start() {
        new Thread(this).start();
    }
    public void run() {
        while (alive)
            try {
                disconnected = Message.DISCONNECTED.equals(dataInputStream.readUTF());
                alive = !disconnected;
            }
            catch(IOException e) {}
        if (disconnected)
            client.onDisconnect();
    }
    public void stop() {
        alive = false;
    }
}
