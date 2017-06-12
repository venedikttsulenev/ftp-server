package netUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReceiver {
    public static void receive(DataInputStream inputStream, String rootPath) {
        try {
            String filename = inputStream.readUTF();
            FileOutputStream output = new FileOutputStream(new File(rootPath + '/' + filename).getAbsolutePath());
            long fileSize = inputStream.readLong();
            int blockSize = inputStream.readInt();
            byte[] filePart = new byte[blockSize];
            int bytesRead;
            long totalBytesRead = 0;
            while (totalBytesRead < fileSize) {
                bytesRead = inputStream.read(filePart);
                totalBytesRead += bytesRead;
                output.write(filePart, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
