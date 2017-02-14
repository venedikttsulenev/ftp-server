public class Args {
    public static int parsePort(String s) throws IllegalArgumentException {
        int port;
        try {
            port = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(s + " is not a number", e);
        }
        if (port < 1 || port > 65535)
            throw new IllegalArgumentException("Port number out of range");
        return port;
    }
    public static String argNotSpecifiedMessage(String argName, String defaultValue) {
        return String.format("%s not specified. Using %s by default", argName, defaultValue);
    }
}
