package netUtils;

public interface MessageHandler {
    void handle(Host host, Session session, String message);
}
