package app;

import concurrentUtils.Channel;
import concurrentUtils.Dispatcher;
import concurrentUtils.ThreadPool;
import netUtils.Host;
import netUtils.MessageHandlerFactory;
import netUtils.Session;

public class Server implements netUtils.Server, Runnable {
    public static final int DEFAULT_PORT = 40001;
    private static final int DEFAULT_SESSIONS_LIMIT = 1024;
    private static final int DEFAULT_CHANNEL_SIZE = 512;
    private final MessageHandlerFactory messageHandlerFactory;
    private final ThreadPool threadPool;
    private final Channel<Runnable> sessionChannel;
    private final int port;
    private final int maxSessions;
    public Server(int port, int maxSessions, int channelSize, MessageHandlerFactory messageHandlerFactory) {
        this.port = port;
        this.maxSessions = maxSessions;
        this.threadPool = new ThreadPool(maxSessions);
        this.sessionChannel = new Channel<>(channelSize);
        new Dispatcher(sessionChannel, threadPool); /* Starts new thread implicitly */
        this.messageHandlerFactory = messageHandlerFactory;
    }
    @Override
    public void onSessionStarted(Host host, Session session) {
        System.out.println(
                host.getAddress()
                + ": Client#" + session.getId()
                + " connected [total: " + threadPool.getSessionsCount() + ']'
        );
    }
    @Override
    public void onSessionFinished(Host host, Session session) {
        System.out.println(
                host.getAddress()
                + ": Client#" + session.getId()
                + " disconnected [total: " + (threadPool.getSessionsCount() - 1) + ']'
        );
    }
    @Override
    public int sessionsRunning() {
        return threadPool.getSessionsCount();
    }
    @Override
    public int maxSessions() {
        return this.maxSessions;
    }
    @Override
    public void run() {
        Host host = new Host(this, port, sessionChannel, messageHandlerFactory);
        host.run();
    }
    public static void main(String[] args) {
        /* Usage: java app.Server <port> <channelSize> <maxSessions> */
        int port = Args.parsePort(args, 0, DEFAULT_PORT);
        int channelSize = Args.parseInt(args, 1, "Channel size", DEFAULT_CHANNEL_SIZE);
        int maxSessions = Args.parseInt(args, 2, "Max amount of sessions", DEFAULT_SESSIONS_LIMIT);

        try {
            Class classFactory = Class.forName("app.PrintMessageHandlerFactory");
            MessageHandlerFactory mHF = (MessageHandlerFactory)classFactory.newInstance();

            Server server = new Server(port, maxSessions, channelSize, mHF);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO: run host in a separate thread and implement command line interface for server management
        *  e.g: shutdown <host address>*/
    }
}
