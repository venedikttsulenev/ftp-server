package netUtils;

public enum Message {
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
    public boolean equals(String s) {
        return mStr.equals(s);
    }
}
