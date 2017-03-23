import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public static final String SERVER_BUSY_MESSAGE = "s_busy";
    public static final String SERVER_CONNECTED_MESSAGE = "s_conn";
    private final Object lock = new Object();
    private int sessions = 0;
    private int maxSessions;
    private int port;
    public Server(int port, int maxSessions) {
        this.port = port;
        this.maxSessions = maxSessions;
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
                    try {
                        Session session = new Session(this, socket, ++sessionID);
                        new Thread(session).start();
                        dataOutputStream.writeUTF(SERVER_CONNECTED_MESSAGE);
                        System.out.println("Client #" + sessionID + " connected [sessions running: " + sessions + ']');
                    } catch (SecurityException | IllegalThreadStateException e) {
                        --sessions; /* Failed to start thread */
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
