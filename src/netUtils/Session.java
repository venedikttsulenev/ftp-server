package netUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Session implements Runnable {
    private Host host;
    private Socket socket;
    private int id;
    private final MessageHandler messageHandler;
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
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeUTF(Message.CONNECTED.toString());
            host.sessionStarted(this);
            String message = dataInputStream.readUTF();
            while (!Message.DISCONNECTED.toString().equals(message)) {
                if (message.charAt(0) == ':')  /* If message starts with ':' then it's user's text message */
                    messageHandler.handle(host, this, message.substring(1));
                message = dataInputStream.readUTF();
            }
        }
        catch (Exception e) {
            messageHandler.handle(host, this, "Error: " + e.getMessage());
        }
        finally {
            host.sessionFinished(this);
        }
    }
}
