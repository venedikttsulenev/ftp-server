package server;

import netUtils.FileSender;

import java.io.DataOutputStream;
import java.io.File;

public class Environment {
    private final File root;
    private final DataOutputStream controlOutputStream;
    private final FileSender fileSender;
    private boolean loggedIn = false;

    public Environment(File root, DataOutputStream controlOutputStream, FileSender fileSender) {
        this.root = root;
        this.controlOutputStream = controlOutputStream;
        this.fileSender = fileSender;
    }

    public File getRoot() {
        return root;
    }

    public DataOutputStream getControlOutputStream() {
        return controlOutputStream;
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public void logIn() {
        loggedIn = true;
    }

    public boolean loggedIn() {
        return loggedIn;
    }
}
