package pt.ipb.dsys.peer.serializable;

import java.io.Serializable;

public class FileID implements Serializable {
    private final byte[] fileId;
    private final String pathname;
    private final int nChunks;

    public FileID(byte[] fileId, String fileDir, int nChunks) {
        this.fileId = fileId;
        this.pathname = fileDir;
        this.nChunks = nChunks;
    }

    public byte[] getFileId() {
        return fileId;
    }
    public String getFilePathname() { return pathname; }
    public int getNChunks() { return nChunks; }

    public boolean equals(FileID fId) {
        return this.fileId.equals(fId.fileId);
    }
    public boolean equals(String pathname) { return this.pathname.equals(pathname); }
}
