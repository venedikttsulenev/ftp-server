package netUtils;

import concurrentUtils.Channel;
import concurrentUtils.Stoppable;
import netUtils.ftp.Reply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Host implements Stoppable {
    private final String hostname;
    private final int cotrolPort;
    private final int dataPort;
    private final String addrString;
    private final Channel<Stoppable> sessionChannel;
    private final Server server;
    private final MessageHandlerFactory messageHandlerFactory;
    private volatile boolean isAlive;
    private final Object controlSocketsLock = new Object();
    private final HashMap<Integer, Socket> controlSocketsWaiting;
    private final DataConnectionListener dataConnectionListener;
    private final ServerSocket controlServerSocket;
    private final ServerSocket dataServerSocket;
    private final File root;
    public Host(File root, HashMap<Integer, Socket> controlSocketsWaiting, String hostname, int controlPort, int dataPort, Channel<Stoppable> channel, MessageHandlerFactory messageHandlerFactory, Server server) throws IOException {
        this.root = root;
        this.server = server;
        this.hostname = hostname;
        this.cotrolPort = controlPort;
        this.dataPort = dataPort;
        this.addrString = hostname + ':' + controlPort;
        this.sessionChannel = channel;
        this.messageHandlerFactory = messageHandlerFactory;
        this.controlSocketsWaiting = controlSocketsWaiting;
        this.controlServerSocket = new ServerSocket(cotrolPort);
        this.dataServerSocket = new ServerSocket(dataPort);
        this.dataConnectionListener = new DataConnectionListener(dataServerSocket);
    }
    public String getAddress() {
        return addrString;
    }
    public void sessionStarted(Session session) {
        server.onSessionStarted(this, session);
    }
    public void sessionFinished(Session session) {
        server.onSessionFinished(this, session);
    }


    private class DataConnectionListener implements Stoppable {
        private final ServerSocket dataServerSocket;
        private volatile boolean active = true;
        public DataConnectionListener(ServerSocket dataServerSocket) {
            this.dataServerSocket = dataServerSocket;
        }

        @Override
        public void run() {
            while (active) {
                try {
                    Socket dataSocket = dataServerSocket.accept();
                    String addr = dataSocket.getInetAddress().getHostName() + ':' + dataSocket.getPort();
                    System.out.println("Data connection attempt from " + addr);
                    int id = new DataInputStream(dataSocket.getInputStream()).readInt();
                    synchronized (controlSocketsLock) {
                        Socket controlSocket = controlSocketsWaiting.get(id);
                        if (controlSocket == null) {
                            System.out.println("Data connection from " + addr + " failed");
                            dataSocket.close();
                        }
                        else {
                            synchronized (controlSocketsLock) {
                                controlSocketsWaiting.remove(id);
                            }
                            sessionChannel.put(new Session(root, Host.this, controlSocket, dataSocket, id, messageHandlerFactory.createMessageHandler()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void stop() {
            active = false;
        }
    }

    @Override
    public void run() {
        isAlive = true;
        try {
            new Thread(dataConnectionListener).start();
            System.out.println("Server started at " + hostname + ':' + cotrolPort + ", " + hostname + ':' + dataPort);
            int sessionID = -1;
            while (isAlive) {
                try {
                    Socket controlSocket = controlServerSocket.accept();
                    controlSocketsWaiting.put(++sessionID, controlSocket);
                    System.out.println("Control connection attempt from " + controlSocket.getInetAddress().getHostName() + ':' + controlSocket.getPort());
                    DataOutputStream controlOutputStream = new DataOutputStream(controlSocket.getOutputStream());
                    controlOutputStream.writeInt(sessionID);

                    if (server.sessionsRunning() >= server.maxSessions())
                        new DataOutputStream(controlSocket.getOutputStream()).writeUTF(String.valueOf(Reply.SERVICE_NOT_AVAILABLE));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stop() {
        dataConnectionListener.stop();
        if (isAlive)
            isAlive = false;
    }
}
