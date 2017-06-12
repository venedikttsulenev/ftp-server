package netUtils.ftp.command;

import netUtils.ftp.Reply;
import server.Environment;

public class HelpCommand extends Command {
    @Override
    public String execute(Environment env) {
        return Reply.HELP_REPLY + " Supported commands:\nUSER\nHELP\nRETR\nLIST";
    }
}
