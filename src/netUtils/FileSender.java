package netUtils;

import java.io.*;

public class FileSender {
    private static final int BLOCK_SIZE = 2048;
    private final DataOutputStream dataOutputStream;

    public FileSender(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public void send(File file) throws IllegalArgumentException, IOException {
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());
        dataOutputStream.writeInt(BLOCK_SIZE);
        if (file.isDirectory())
            throw new IllegalArgumentException();
        FileInputStream input = new FileInputStream(file);
        int bytesRead;
        byte bytes[] = new byte[BLOCK_SIZE];
        while (bytes.length == (bytesRead = input.read(bytes)))
            dataOutputStream.write(bytes);
        if (bytesRead != -1)
            dataOutputStream.write(bytes, 0, bytesRead);
    }
}
