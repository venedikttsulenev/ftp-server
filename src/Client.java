import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static final String DEFAULT_HOSTNAME = "localhost";
    public static void main(String[] args) {
        String hostname = DEFAULT_HOSTNAME;
        int port = Server.DEFAULT_PORT;
        if (args.length > 1)
            hostname = args[1];
        if (args.length > 2)
            port = Integer.parseInt(args[2]);
        try {
            Socket socket = new Socket(hostname, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String message = null;
            Scanner scanner = new Scanner(System.in);
            while (!Server.MESSAGE_STOP.equals(message) && scanner.hasNextLine()) {
                message = scanner.nextLine();
                dataOutputStream.writeUTF(message);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
