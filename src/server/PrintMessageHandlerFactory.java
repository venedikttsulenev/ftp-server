package server;

import netUtils.MessageHandler;
import netUtils.MessageHandlerFactory;

public class PrintMessageHandlerFactory implements MessageHandlerFactory {
    @Override
    public MessageHandler createMessageHandler() {
        return new PrintMessageHandler();
    }
}
