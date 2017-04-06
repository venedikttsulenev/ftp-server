package app;

import netUtils.Message;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static String humanReadable(Message message, String messageString) {
        switch (message) {
            case SERVER_BUSY:
                return "Server is busy now :C. Please, wait";
            case CONNECTED:
                return "Connection established.";
            case DISCONNECTED:
                return "Server disconnected";
            default:
                return "Unknown message: \"" + messageString + '\"';
        }
    }
    public static void main(String[] args) {
        /* Usage: java app.Client <hostname> <port> */
        try {
            String hostname = Args.parseString(args, 0, "Host", Client.DEFAULT_HOSTNAME);
            int port = Args.parsePort(args, 1, Server.DEFAULT_PORT);
            try (Socket socket = new Socket(hostname, port);
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream()))
            {
                String serverSays = dataInputStream.readUTF();
                if (Message.SERVER_BUSY.equals(serverSays))
                    serverSays = dataInputStream.readUTF();
                if (Message.CONNECTED.equals(serverSays)) {
                    System.out.println("Type ':message' to send message. Type 'quit' to quit.");
                    Console console = System.console();
                    String input;
                    boolean quit = false;
                    while (!quit) {
                        input = console.readLine();
                        if ("quit".equals(input))
                            quit = true;
                        else if (input.charAt(0) == ':')  /* User text message starts with ':' */
                            dataOutputStream.writeUTF(input);
                        else
                            System.out.println("What?");
                    }
                }
                /* TODO: put this writeUTF in finally {...} */
                dataOutputStream.writeUTF(Message.DISCONNECTED.toString());
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