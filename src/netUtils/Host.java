package netUtils;

import concurrentUtils.Channel;
import concurrentUtils.Stoppable;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Host implements Stoppable {
    private final int port;
    private final String addrString;
    private final Channel<Stoppable> sessionChannel;
    private final Server server;
    private final MessageHandlerFactory messageHandlerFactory;
    private volatile boolean isAlive;
    public Host(Server server, int port, Channel<Stoppable> channel, MessageHandlerFactory messageHandlerFactory) {
        this.server = server;
        this.port = port;
        this.addrString = "localhost:" + port;
        this.sessionChannel = channel;
        this.messageHandlerFactory = messageHandlerFactory;
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
    @Override
    public void run() {
        isAlive = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started at " + addrString);
            int sessionID = -1;
            while (isAlive) {
                try {
                    Socket socket = serverSocket.accept();
                    Session session = new Session(this, socket, ++sessionID, messageHandlerFactory.createMessageHandler());
                    if (server.sessionsRunning() == server.maxSessions())
                        new DataOutputStream(socket.getOutputStream()).writeUTF(Message.SERVER_BUSY.toString());
                    sessionChannel.put(session);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void stop() {
        if (isAlive)
            isAlive = false;
        /* serverSocket will be closed automatically
        * as soon as end of try-with-resources block in run() is reached */
    }
}
