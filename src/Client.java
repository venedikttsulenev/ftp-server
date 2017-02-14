import java.io.Console;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String DEFAULT_HOSTNAME = "localhost";
    public static void main(String[] args) {
        /* Usage: java Client <hostname> <port> */
        try {
            String hostname = DEFAULT_HOSTNAME;
            int port = Server.DEFAULT_PORT;
            if (args.length > 1)
                port = Args.parsePort(args[1]);
            else
                System.out.println(Args.argNotSpecifiedMessage("Port", String.valueOf(port)));
            if (args.length > 0)
                hostname = args[0];
            else
                System.out.println(Args.argNotSpecifiedMessage("Host", hostname));
            Socket socket = new Socket(hostname, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String message = null;
            Console console = System.console();
            while (!Server.MESSAGE_STOP.equals(message)) {
                message = console.readLine();
                dataOutputStream.writeUTF(message);
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Unknown host \"" + e.getMessage() + '\"');
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}