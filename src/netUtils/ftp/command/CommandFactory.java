package netUtils.ftp.command;

import java.io.File;

public class CommandFactory {
    public static Command commandByString(String s) {
        String[] strings = s.split("\\s");
        switch (strings[0].toUpperCase()) {
            case "USER":
                return new UserCommand(strings.length > 1 ? strings[1] : null);
            case "HELP":
                return new HelpCommand();
            case "RETR":
                return new RetrCommand(strings.length > 1 ? strings[1] : null);
            case "LIST":
                return new ListCommand();
            default:
                return new UnknownCommand();
        }
    }
}
