package app;

import concurrentUtils.Channel;
import concurrentUtils.Dispatcher;
import concurrentUtils.Stoppable;
import concurrentUtils.ThreadPool;
import netUtils.Host;
import netUtils.MessageHandlerFactory;
import netUtils.Session;

public class Server implements netUtils.Server, Stoppable {
    public static final int DEFAULT_PORT = 40001;
    private static final int DEFAULT_SESSIONS_LIMIT = 1024;
    private static final int DEFAULT_CHANNEL_SIZE = 512;
    private final ThreadPool threadPool;
    private final int maxSessions;
    private final Host host;
    private final Dispatcher dispatcher;
    public Server(int port, int maxSessions, int channelSize, MessageHandlerFactory messageHandlerFactory) {
        this.maxSessions = maxSessions;
        this.threadPool = new ThreadPool(maxSessions);
        Channel<Stoppable> sessionChannel = new Channel<>(channelSize);
        this.dispatcher = new Dispatcher(sessionChannel, threadPool); /* Starts new thread implicitly */
        this.host = new Host(this, port, sessionChannel, messageHandlerFactory);
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
        host.run();
    }
    @Override
    public void stop() {
        System.out.println("Shutting down...");
        host.stop();
        dispatcher.stop();
        threadPool.stop();
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
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO: run multiple hosts in separate threads and implement command line interface for server management
        *  e.g: shutdown <host address> */
    }
}
