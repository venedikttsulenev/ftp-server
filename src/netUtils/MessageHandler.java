package netUtils;

public interface MessageHandler {
    String handle(Host host, Session session, String message);
}
