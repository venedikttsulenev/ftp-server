package netUtils;

import concurrentUtils.Channel;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Host implements Runnable {
    private final int port;
    private final String addrString;
    private final Channel<Runnable> sessionChannel;
    private final Server server;
    public Host(Server server, int port, Channel<Runnable> channel) {
        this.server = server;
        this.port = port;
        this.addrString = "localhost:" + port;
        this.sessionChannel = channel;
    }
    public String getAddress() {
        return addrString;
    }
    public void sessionStarted(Session session) {
        server.onSessionStarted(this, session);
    }
    public void sessionMessage(Session session, String message) {
        server.onMessageReceived(this, session, message);
    }
    public void sessionFinished(Session session) {
        server.onSessionFinished(this, session);
    }
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started at " + addrString);
            int sessionID = -1;
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Session session = new Session(this, socket, ++sessionID);
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
}