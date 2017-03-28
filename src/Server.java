import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public static final String SERVER_BUSY_MESSAGE = "s_busy";
    public static final String SERVER_CONNECTED_MESSAGE = "s_conn";
    private static final int DEFAULT_SESSION_CHANNEL_SIZE = 512;
    private final Object sessionsCounterLock = new Object();
    private final int maxSessions;
    private final int port;
    private final int id;
    private int sessions = 0;
    public Server(int port, int maxSessions, int id) {
        this.port = port;
        this.maxSessions = maxSessions;
        this.id = id;
    }
    public void sessionStarted(int sessionID) {
        synchronized (sessionsCounterLock) {
            ++sessions;
            System.out.println("Server #" + this.id + ": Client #" + sessionID + " connected [" + sessions + ']');
        }
    }
    public void sessionMessage(int sessionID, String message) {
        System.out.println("Server #" + this.id + ": Client #" + sessionID + ": " + message);
    }
    public void sessionFinished(int sessionID) {
        synchronized (sessionsCounterLock) {
            --sessions;
            System.out.println("Server #" + this.id + ": Client #" + sessionID + " disconnected [" + sessions + ']');
        }
    }
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ThreadPool threadPool = new ThreadPool(maxSessions);
            Channel<Session> channel = new Channel<>(DEFAULT_SESSION_CHANNEL_SIZE);
            Dispatcher dispatcher = new Dispatcher(channel, threadPool);
            new Thread(dispatcher).start();
            System.out.println("Server #" + this.id + " started at localhost:" + port + ". Max sessions: " + maxSessions);
            int sessionID = -1;
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    synchronized (sessionsCounterLock) {
                        if (sessions == maxSessions)
                            new DataOutputStream(socket.getOutputStream()).writeUTF(SERVER_BUSY_MESSAGE);
                    }
                    Session session = new Session(this, socket, ++sessionID);
                    threadPool.execute(session);
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
