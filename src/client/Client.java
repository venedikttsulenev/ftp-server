package client;

import commons.Args;
import netUtils.FileReceiver;
import netUtils.ftp.Reply;
import server.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client implements Runnable {
    private final static String DOWNLOADS_DIR = "/Users/venedikttsulenev/Downloads";
    private static final String DEFAULT_HOSTNAME = "localhost";
    private final String hostname;
    private final int controlPort;
    private final int dataPort;
    public Client(String hostname, int controlPort, int dataPort) {
        this.hostname = hostname;
        this.controlPort = controlPort;
        this.dataPort = dataPort;
    }
    public void run() {
        try (Socket controlSocket = new Socket(hostname, controlPort);
             DataOutputStream controlOutputStream = new DataOutputStream(controlSocket.getOutputStream());
             DataInputStream controlInputStream = new DataInputStream(controlSocket.getInputStream());
             Socket dataSocket = new Socket(hostname, dataPort);
             DataInputStream dataInputStream = new DataInputStream(dataSocket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(dataSocket.getOutputStream())
        )
        {
            int id = controlInputStream.readInt();
            System.out.println("My ID: "  + id);
            dataOutputStream.writeInt(id);
            System.out.println(controlInputStream.readUTF());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
            while (true) {
                String line = reader.readLine();
                String spl[] = line.split("\\s");
                controlOutputStream.writeUTF(line);
                String response = controlInputStream.readUTF();
                System.out.println(response);
                if (spl[0].toUpperCase().equals("RETR") && response.contains(Reply.OK_TRANSFER_STARTING)) {
                    FileReceiver.receive(dataInputStream, DOWNLOADS_DIR);
                    System.out.println("File '" + spl[1] + "' received");
                    System.out.println(controlInputStream.readUTF());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        /* Usage: java server.Client <hostname> <controlPort> <dataPort> <directory> */
        try {
            String hostname = Args.parseString(args, 0, "Host", Client.DEFAULT_HOSTNAME);
            int controlPort = Args.parsePort(args, 1, Server.DEFAULT_CONTROL_PORT);
            int dataPort = Args.parsePort(args, 2, Server.DEFAULT_DATA_PORT);
            new Client(hostname, controlPort, dataPort)
                    .run();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}