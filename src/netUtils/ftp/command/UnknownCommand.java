package netUtils.ftp.command;

import netUtils.ftp.Reply;
import server.Environment;

public class UnknownCommand extends Command {
    @Override
    public String execute(Environment env) {
        return Reply.COMMAND_NOT_IMPLEMENTED;
    }
}
