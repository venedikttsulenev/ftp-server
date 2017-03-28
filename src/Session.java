import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Session implements Runnable {
    private Server server;
    private Socket socket;
    private int id;
    public Session(Server server, Socket socket, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
    }
    public void run() {
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeUTF(Server.SERVER_CONNECTED_MESSAGE);
            server.sessionStarted(this.id);
            String message = dataInputStream.readUTF();
            while (!Client.DISCONNECT_MESSAGE.equals(message)) {
                server.sessionMessage(this.id, message);
                message = dataInputStream.readUTF();
            }
        }
        catch (Exception e) {
            server.sessionMessage(id, " error: " + e.getMessage());
        }
        finally {
            server.sessionFinished(this.id);
        }
    }
}
