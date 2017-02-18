public class Args {
    public static int parsePort(String args[], int index, int defaultValue) throws IllegalArgumentException {
        int port = Args.parseInt(args, index, "Port", defaultValue);
        if (port < 1 || port > 65535)
            throw new IllegalArgumentException("Port number out of range");
        return port;
    }
    public static int parseInt(String args[], int index, String name, int defaultValue) throws IllegalArgumentException {
        int i = defaultValue;
        try {
            if (args.length > index)
                i = Integer.parseInt(args[index]);
            else
                printArgNotSpecifiedMessage(name, String.valueOf(defaultValue));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(args[index] + " is not a number", e);
        }
        return i;
    }
    public static String parseString(String args[], int index, String name, String defaultValue) {
        String s = defaultValue;
        if (args.length > index)
            s = args[index];
        else
            printArgNotSpecifiedMessage(name, defaultValue);
        return s;
    }
    public static void printArgNotSpecifiedMessage(String argName, String defaultValue) {
        System.out.println(argName + " not specified. Using " + defaultValue + " by default");
    }
}
