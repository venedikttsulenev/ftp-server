import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int DEFAULT_PORT = 40001;
    static final int DEFAULT_SESSIONS_LIMIT = 1024;
    static final String MESSAGE_STOP = "stop";
    private static final Object lock = new Object();
    private static int sessions = 0;
    public static void increaseSessions() { synchronized (lock) {++sessions;} }
    public static void decreaseSessions() { synchronized (lock) {--sessions;} }
    public static int getSessions() { synchronized (lock) {return sessions;} }
    public static void main(String[] args) {
        /* Usage: java Server <port> <maxSessions> */
        try {
            int port = Args.parsePort(args, 0, DEFAULT_PORT);
            int maxSessions = Args.parseInt(args, 1, "Max amount of sessions", DEFAULT_SESSIONS_LIMIT);
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started at localhost:" + port + ". Max sessions: " + maxSessions);
                int sessionID = -1;
                while (true) {
                    while (getSessions() < maxSessions) {
                        Socket socket = serverSocket.accept();
                        increaseSessions();
                        try {
                            new Thread(new Session(socket, ++sessionID)).start();
                        } catch (Throwable e) {
                            decreaseSessions(); /* Поток не стартовал */
                        }
                    }
                    Thread.sleep(500); /* Снижаем нагрузку на ЦП при достижении maxSessions */
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
