package netUtils.ftp.command;

import netUtils.ftp.Reply;
import server.Environment;

class UserCommand extends Command {
    private final String username;
    public UserCommand(String username) {
        this.username = username;
    }

    @Override
    public String execute(Environment env) {
        if ("anonymous".equals(username)) {
            env.logIn();
            return Reply.USER_LOGGED_IN;
        }
        return Reply.NOT_LOGGED_IN;
    }
}
