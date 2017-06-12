package netUtils.ftp.command;

import server.Environment;

abstract public class Command {
    abstract public String execute(Environment env);
}
