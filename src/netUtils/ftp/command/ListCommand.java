package netUtils.ftp.command;

import netUtils.ftp.Reply;
import server.Environment;

import java.io.File;

public class ListCommand extends Command {
    @Override
    public String execute(Environment env) {
        if (!env.loggedIn())
            return Reply.NOT_LOGGED_IN;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (File f : env.getRoot().listFiles()) {
                stringBuilder.append(f.getName());
                stringBuilder.append('\n');
            }
        } catch (NullPointerException e) {
            stringBuilder.append("Empty");
        }
        return Reply.DIRECTORY_STATUS + ' ' + stringBuilder.toString();
    }
}
