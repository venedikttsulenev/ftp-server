package netUtils.ftp;

public class Reply {
    public static final String OK_TRANSFER_STARTING = "125 OK_TRANSFER_STARTING";
    public static final String COMMAND_NOT_IMPLEMENTED = "202 COMMAND_NOT_IMPLEMENTED";
    public static final String SERVICE_READY_FOR_NEW_USER = "220 SERVICE_READY_FOR_NEW_USER";
    public static final String USER_LOGGED_IN = "230 USER_LOGGED_IN";
    public static final String REQUESTED_ACTION_COMPLETED = "250 REQUESTED_ACTION_COMPLETED";
    public static final String SERVICE_NOT_AVAILABLE = "421 SERVICE_NOT_AVAILABLE";
    public static final String REQUESTED_ACTION_ABORTED_LOCAL_ERROR = "451 REQUESTED_ACTION_ABORTED_LOCAL_ERROR";
    public static final String NOT_LOGGED_IN = "530 NOT_LOGGED_IN";
    public static final String REQUESTED_ACTION_ABORTED_FILE_UNAVAILABLE = "550 REQUESTED_ACTION_ABORTED_FILE_UNAVAILABLE";
    public static final String HELP_REPLY = "211";
    public static final String DIRECTORY_STATUS = "212";
}
