import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int DEFAULT_PORT = 40001;
    static final String MESSAGE_STOP = "stop";
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 1)
            port = Integer.parseInt(args[1]);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String message = null;
            while (!MESSAGE_STOP.equals(message)) {
                message = dataInputStream.readUTF();
                System.out.println("Message: " + message);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
