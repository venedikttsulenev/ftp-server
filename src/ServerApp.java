public class ServerApp {
    public static final int DEFAULT_PORT = 40001;
    private static final int DEFAULT_SESSIONS_LIMIT = 1024;
    public static void main(String[] args) {
        /* Usage: java ServerApp <port> <maxSessions> */
        int port = Args.parsePort(args, 0, DEFAULT_PORT);
        int maxSessions = Args.parseInt(args, 1, "Max amount of sessions", DEFAULT_SESSIONS_LIMIT);

        Server server = new Server(port, maxSessions, 0);
        server.run();

        /* TODO: run server in separate thread and implement command line interface for server management
        *  e.g: shutdown <server id>*/
    }
}
