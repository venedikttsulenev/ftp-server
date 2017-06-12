package server;

import commons.Args;
import concurrentUtils.Channel;
import concurrentUtils.Dispatcher;
import concurrentUtils.Stoppable;
import concurrentUtils.ThreadPool;
import netUtils.Host;
import netUtils.MessageHandlerFactory;
import netUtils.Session;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Server implements netUtils.Server, Stoppable {
    public static final int DEFAULT_CONTROL_PORT = 40001;
    public static final int DEFAULT_DATA_PORT = 40002;
    private static final int DEFAULT_SESSIONS_LIMIT = 1024;
    private static final int DEFAULT_CHANNEL_SIZE = 512;
    private static final String DEFAULT_DIRECTORY = "/Users/venedikttsulenev/Public/";
    private final ThreadPool threadPool;
    private final int maxSessions;
    private final Host host;
    private final Dispatcher dispatcher;
    private final HashMap<Integer, Socket> controlSocketsWaiting;
    public Server(File root, int dataPort, int maxSessions, int channelSize, MessageHandlerFactory messageHandlerFactory, int controlPort) throws IOException {
        this.maxSessions = maxSessions;
        this.controlSocketsWaiting = new HashMap<>(maxSessions);
        this.threadPool = new ThreadPool(maxSessions);
        Channel<Stoppable> sessionChannel = new Channel<>(channelSize);
        this.dispatcher = new Dispatcher(sessionChannel, threadPool); /* Starts new thread implicitly */
        this.host = new Host(root, controlSocketsWaiting, "localhost", controlPort, dataPort, sessionChannel, messageHandlerFactory, this);
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

        /* Usage: java server.Server <controlPort> <dataPort> <directory> <channelSize> <maxSessions> */
        int controlPort = Args.parsePort(args, 0, DEFAULT_CONTROL_PORT);
        int dataPort = Args.parsePort(args, 1, DEFAULT_DATA_PORT);
        int channelSize = Args.parseInt(args, 2, "Channel size", DEFAULT_CHANNEL_SIZE);
        int maxSessions = Args.parseInt(args, 3, "Max amount of sessions", DEFAULT_SESSIONS_LIMIT);
        String directory = Args.parseString(args, 4, "Root directory", DEFAULT_DIRECTORY);

        File root = new File(directory);

        try {
            Class classFactory = Class.forName("server.PrintMessageHandlerFactory");
            MessageHandlerFactory mHF = (MessageHandlerFactory)classFactory.newInstance();

            Server server = new Server(root, dataPort, maxSessions, channelSize, mHF, controlPort);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO: run multiple hosts in separate threads and implement command line interface for server management
        *  e.g: shutdown <host address> */
    }
}
