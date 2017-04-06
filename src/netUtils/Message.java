package netUtils;

public enum Message {
    UNKNOWN     (null),
    CONNECTED   ("conn"),
    DISCONNECTED("disc"),
    SERVER_BUSY ("busy");

    private final String mStr;
    Message(String string) {
        this.mStr = string;
    }
    @Override
    public String toString() {
        return mStr;
    }
    public static Message recognize(String message) throws NullPointerException {
        if (null == message)
            throw new NullPointerException();
        switch (message) {
            case "conn":
                return CONNECTED;
            case "busy":
                return SERVER_BUSY;
            case "disc":
                return DISCONNECTED;
            default:
                return UNKNOWN;
        }
    }
}
