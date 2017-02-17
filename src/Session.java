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
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
            String message = null;
            while (!Server.MESSAGE_STOP.equals(message)) {
                message = dataInputStream.readUTF();
                System.out.println("Message: " + message);
            }
        }
        catch (IOException e) {
            System.out.println("Session #" + sessionNumber + ": " + e.getMessage());
        }
    }
}
