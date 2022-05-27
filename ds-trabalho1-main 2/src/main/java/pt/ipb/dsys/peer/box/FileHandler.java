package pt.ipb.dsys.peer.box;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
    public static byte[] GetFileBytes(String fileDir) throws IOException {
        FileInputStream fIn = new FileInputStream(fileDir);
        byte[] bytes;

        bytes = fIn.readAllBytes();
        fIn.close();

        return bytes;
    }

    public static void WriteFileFromBytes(byte[] bytes, String fileDir) throws IOException {
        FileOutputStream fOut = new FileOutputStream(fileDir);

        for (byte b: bytes)
            fOut.write(b);

        fOut.flush();
        fOut.close();
    }
}
