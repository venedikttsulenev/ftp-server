package netUtils;

public interface Server {
    void onSessionStarted(Host host, Session session);
    void onSessionFinished(Host host, Session session);
    int sessionsRunning();
    int maxSessions();
}
