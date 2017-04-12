package netUtils;

import concurrentUtils.Stoppable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Stoppable {
    private final Host host;
    private final Socket socket;
    private final int id;
    private final MessageHandler messageHandler;
    private volatile boolean stopped = false;
    public Session(Host host, Socket socket, int id, MessageHandler messageHandler) {
        this.host = host;
        this.socket = socket;
        this.id = id;
        this.messageHandler = messageHandler;
    }
    public int getId() {
        return id;
    }
    @Override
    public void run() {
        host.sessionStarted(this);
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeUTF(Message.CONNECTED.toString());
            String message = dataInputStream.readUTF();
            while (!Message.DISCONNECTED.toString().equals(message)) {
                if (message.charAt(0) == ':')  /* If message starts with ':' then it's user's text message */
                    messageHandler.handle(host, this, message.substring(1));
                message = dataInputStream.readUTF();
            }
        }
        catch (Exception e) {
            if (!stopped)
                messageHandler.handle(host, this, "Error: " + e.getMessage());
        }
        finally {
            if (!stopped)
                host.sessionFinished(this);
        }
    }
    @Override
    public void stop() {
        stopped = true;
        if (!socket.isClosed()) {
            try {
                new DataOutputStream(socket.getOutputStream()).writeUTF(Message.DISCONNECTED.toString());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
