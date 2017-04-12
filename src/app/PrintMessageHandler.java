package app;

import netUtils.Host;
import netUtils.MessageHandler;
import netUtils.Session;

public class PrintMessageHandler implements MessageHandler {
    public void handle(Host host, Session session, String message) throws NullPointerException {
        if (null == host || null == session || null == message)
            throw new NullPointerException();
        System.out.println(host.getAddress() + ": #" + session.getId() + " says: " + message);
    }
}
