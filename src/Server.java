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
            try (ServerSocket serverSocket = new ServerSocket(port))
            {
                while (true) {
                    Socket socket = serverSocket.accept();
                    Thread thread = new Thread(new Session(socket, 0));
                    thread.start();
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
