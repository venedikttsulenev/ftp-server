package app;

import netUtils.MessageHandler;

public class PrintMessageHandler implements MessageHandler {
    public String handle(String message) throws NullPointerException {
        if (null == message)
            throw new NullPointerException();
        System.out.println(message);
        return message;
    }
}
