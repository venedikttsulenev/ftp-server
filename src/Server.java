import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int DEFAULT_PORT = 40001;
    static final String MESSAGE_STOP = "stop";
    public static void main(String[] args) {
        /* Usage: java Server <port> */
        try {
            int port = DEFAULT_PORT;
            if (args.length > 0)
                port = Args.parsePort(args[0]);
            else
                System.out.println(Args.argNotSpecifiedMessage("Port", String.valueOf(port)));
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String message = null;
            while (!MESSAGE_STOP.equals(message)) {
                message = dataInputStream.readUTF();
                System.out.println("Message: " + message);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
