import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Runnable {
    private Socket socket;
    private int sessionNumber;
    public Session(Socket socket, int sessionNumber) {
        this.socket = socket;
        this.sessionNumber = sessionNumber;
    }
    public void run() {
        Server.increaseSessions();
        System.out.println("Client #" + sessionNumber + " connected" + " [" + Server.getSessions() + ']');
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String message = dataInputStream.readUTF();
            while (!Server.MESSAGE_STOP.equals(message)) {
                System.out.println("Client #" + sessionNumber + ": " + message);
                message = dataInputStream.readUTF();
            }
        }
        catch (IOException e) {
            System.out.println("Session #" + sessionNumber + " error: " + e.getMessage());
        }
        Server.decreaseSessions();
        System.out.println("Client #" + sessionNumber + " disconnected" + " [" + Server.getSessions() + ']');
    }
}
