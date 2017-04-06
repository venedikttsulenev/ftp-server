package netUtils;

public interface Server {
    void onSessionStarted(Host host, Session session);
    void onMessageReceived(Host host, Session session, String message);
    void onSessionFinished(Host host, Session session);
    int sessionsRunning();
    int maxSessions();
}
