import java.io.DataInputStream;
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
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String message = dataInputStream.readUTF();
            while (!Client.DISCONNECT_MESSAGE.equals(message)) {
                System.out.println("Client #" + id + ": " + message);
                message = dataInputStream.readUTF();
            }
        }
        catch (Exception e) {
            System.out.println("Session #" + id + " error: " + e.getMessage());
        }
        finally {
            server.sessionFinished(this.id);
        }
    }
}
