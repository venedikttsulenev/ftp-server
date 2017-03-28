import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static final String DISCONNECT_MESSAGE = "c_disc";
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static String humanReadable(String serverMessage) {
        switch (serverMessage) {
            case Server.SERVER_CONNECTED_MESSAGE:
                return "Connected.";
            case Server.SERVER_BUSY_MESSAGE:
                return "Server is busy now :C. Please, wait.";
            default:
                return "Unknown message: '" + serverMessage + '\'';
        }
    }
    public static void main(String[] args) {
        /* Usage: java Client <hostname> <port> */
        try {
            String hostname = Args.parseString(args, 0, "Host", Client.DEFAULT_HOSTNAME);
            int port = Args.parsePort(args, 1, ServerApp.DEFAULT_PORT);
            try (Socket socket = new Socket(hostname, port);
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream()))
            {
                String serverSays = null;
                while (!Server.SERVER_CONNECTED_MESSAGE.equals(serverSays)) {
                    serverSays = dataInputStream.readUTF();
                    System.out.println(humanReadable(serverSays));
                }
                System.out.println("Type ':message' to send message. Type 'quit' to quit.");
                Console console = System.console();
                String input;
                boolean quit = false;
                while (!quit) {
                    input = console.readLine();
                    if ("quit".equals(input)) {         /* 'quit' command */
                        quit = true;
                        dataOutputStream.writeUTF(DISCONNECT_MESSAGE);
                    }
                    else if (input.charAt(0) == ':')    /* message starts with ':' */
                        dataOutputStream.writeUTF(input.substring(1));
                    else
                        System.out.println("What?");
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