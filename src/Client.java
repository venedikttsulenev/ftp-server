import java.io.Console;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String DEFAULT_HOSTNAME = "localhost";
    public static void main(String[] args) {
        /* Usage: java Client <hostname> <port> */
        try {
            String hostname = Args.parseString(args, 0, "Host", DEFAULT_HOSTNAME);
            int port = Args.parsePort(args, 1, Server.DEFAULT_PORT);
            try (Socket socket = new Socket(hostname, port);
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream()))
            {
                Console console = System.console();
                String message = null;
                while (!Server.MESSAGE_STOP.equals(message)) {
                    message = console.readLine();
                    dataOutputStream.writeUTF(message);
                }
            }
            catch (UnknownHostException e) {
                System.out.println("Unknown host \"" + e.getMessage() + '\"');
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}