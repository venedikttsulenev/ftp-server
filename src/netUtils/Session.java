package netUtils;

import concurrentUtils.Stoppable;
import netUtils.ftp.command.Command;
import netUtils.ftp.command.CommandFactory;
import netUtils.ftp.Reply;
import server.Environment;

import java.io.*;
import java.net.Socket;

public class Session implements Stoppable {
    private final Host host;
    private final Socket controlSocket;
    private final Socket dataSocket;
    private final int id;
    private final MessageHandler messageHandler;
    private final Environment environment;
    private final DataInputStream controlInputStream, dataInputStream;
    private final DataOutputStream controlOutputStream, dataOutputStream;
    private volatile boolean stopped = false;
    public Session(File root, Host host, Socket controlSocket, Socket dataSocket, int id, MessageHandler messageHandler) throws IOException {
        this.host = host;
        this.controlSocket = controlSocket;
        this.dataSocket = dataSocket;
        this.id = id;
        this.messageHandler = messageHandler;
        this.controlInputStream = new DataInputStream(controlSocket.getInputStream());
        this.controlOutputStream = new DataOutputStream(controlSocket.getOutputStream());
        this.dataInputStream = new DataInputStream(dataSocket.getInputStream());
        this.dataOutputStream = new DataOutputStream(dataSocket.getOutputStream());
        this.environment = new Environment(root, controlOutputStream, new FileSender(dataOutputStream));
    }
    public int getId() {
        return id;
    }
    @Override
    public void run() {
        host.sessionStarted(this);

        try (DataOutputStream controlOutputStream = new DataOutputStream(controlSocket.getOutputStream());
             DataInputStream controlInputStream = new DataInputStream(controlSocket.getInputStream())) {
            controlOutputStream.writeUTF(Reply.SERVICE_READY_FOR_NEW_USER);
            while (!stopped) {
                String commandStr = controlInputStream.readUTF();
                messageHandler.handle(host, this, commandStr);
                Command command = CommandFactory.commandByString(commandStr);
                String reply = command.execute(environment);
                messageHandler.handle(host, this, "Responded: " + reply);
                controlOutputStream.writeUTF(reply);
            }
        }
        catch (Exception e) {
            if (!stopped)
                messageHandler.handle(host, this, "Error: " + e.getMessage());
        }
        finally {
            if (!stopped)
                host.sessionFinished(this);
        }
    }
    @Override
    public void stop() {
        stopped = true;
        if (!controlSocket.isClosed()) {
            try {
                controlSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
