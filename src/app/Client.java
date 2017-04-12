package app;

import netUtils.Message;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
    private static final String DEFAULT_HOSTNAME = "localhost";
    private final String hostname;
    private final int port;
    private boolean quit = false;
    private volatile boolean serverDisconnected = false;
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    public void onDisconnect() {
        System.out.println("Server disconnected.");
        serverDisconnected = true;
    }
    public void run() {
        try (Socket socket = new Socket(hostname, port)) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            ConnectionChecker connectionChecker = new ConnectionChecker(dataInputStream, this);
            try {
                String serverSays = dataInputStream.readUTF();
                if (Message.SERVER_BUSY.equals(serverSays)) {
                    System.out.println("Server is busy now :C. Please, wait");
                    serverSays = dataInputStream.readUTF();
                }
                if (Message.CONNECTED.equals(serverSays)) {
                    System.out.println("Connection established.");
                    System.out.println("Type ':message' to send message. Type 'quit' to quit.");
                    Console console = System.console();
                    connectionChecker.start();
                    while (!quit && !serverDisconnected) {
                        if (console.reader().ready()) {
                            String input = console.readLine();
                            if (input.length() > 0) {
                                if ("quit".equals(input))
                                    quit = true;
                                else if (input.charAt(0) == ':')  /* User text message starts with ':' */
                                    dataOutputStream.writeUTF(input);
                                else
                                    System.out.println("What?");
                            }
                        }
                    }
                }
            }
            finally {
                if (!serverDisconnected) {
                    dataOutputStream.writeUTF(Message.DISCONNECTED.toString());
                    connectionChecker.stop();
                }
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Unknown host \"" + e.getMessage() + '\"');
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        /* Usage: java app.Client <hostname> <port> */
        try {
            String hostname = Args.parseString(args, 0, "Host", Client.DEFAULT_HOSTNAME);
            int port = Args.parsePort(args, 1, Server.DEFAULT_PORT);
            new Client(hostname, port).run();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}