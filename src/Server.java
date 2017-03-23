import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public static final String SERVER_BUSY_MESSAGE = "s_busy";
    public static final String SERVER_CONNECTED_MESSAGE = "s_conn";
    private static final int DEFAULT_CHANNEL_SIZE = 512;
    private final Object lock = new Object();
    private final int maxSessions;
    private final int port;
    private int sessions = 0;
    public Server(int port, int maxSessions) {
        this.port = port;
        this.maxSessions = maxSessions;
    }
    public void sessionFailed(int sessionID) {
        System.out.println("Failed to start session #" + sessionID);
        sessionFinished(sessionID);
    }
    public void sessionFinished(int sessionID) {
        synchronized (lock) {
            if (sessions == maxSessions)
                lock.notifyAll();
            --sessions;
            System.out.println("Client #" + sessionID + " disconnected [sessions running: " + sessions + ']');
        }
    }
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Channel channel = new Channel(DEFAULT_CHANNEL_SIZE);
            Dispatcher dispatcher = new Dispatcher(channel, this);
            new Thread(dispatcher).start();
            System.out.println("Server started at localhost:" + port + ". Max sessions: " + maxSessions);
            int sessionID = -1;
            while (true) {
                Socket socket = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                synchronized (lock) {
                    if (sessions == maxSessions) {
                        dataOutputStream.writeUTF(SERVER_BUSY_MESSAGE);
                        do {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                            }
                        } while (sessions == maxSessions);
                    }
                    ++sessions;
                    Session session = new Session(this, socket, ++sessionID);
                    channel.put(session);
                    dataOutputStream.writeUTF(SERVER_CONNECTED_MESSAGE);
                    System.out.println("Client #" + sessionID + " connected [sessions running: " + sessions + ']');
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
