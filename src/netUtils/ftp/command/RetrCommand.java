package netUtils.ftp.command;

import netUtils.ftp.Reply;
import server.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RetrCommand extends Command {
    private final String filename;
    public RetrCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public String execute(Environment env) {
        if (!env.loggedIn())
            return Reply.NOT_LOGGED_IN;
        try {
            env.getControlOutputStream().writeUTF(Reply.OK_TRANSFER_STARTING);
            System.out.println(env.getRoot().getAbsolutePath() + '/' + filename);
            env.getFileSender().send(new File(env.getRoot().getAbsolutePath() + '/' + filename));
            return Reply.REQUESTED_ACTION_COMPLETED;
        } catch (FileNotFoundException e) {
            return Reply.REQUESTED_ACTION_ABORTED_FILE_UNAVAILABLE;
        } catch (IOException e) {
            return Reply.REQUESTED_ACTION_ABORTED_LOCAL_ERROR;
        }
    }
}
