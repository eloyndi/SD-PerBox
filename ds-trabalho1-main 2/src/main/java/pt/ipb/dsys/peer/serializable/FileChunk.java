package pt.ipb.dsys.peer.serializable;

import java.io.Serializable;

public class FileChunk implements Serializable {
    private final byte[] fileId;
    private final int chunkNumber;
    private final byte[] content;

    public FileChunk(byte[] fileId, int chunkNumber, byte[] content) {
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        this.content = content;
    }

    public byte[] getFileID() { return fileId; }
    public int getChunkNumber() { return chunkNumber; }
    public byte[] getContent() { return content; }

    public boolean equals(byte[] fileId, int chunkNumber) {
        return this.fileId.equals(fileId) && this.chunkNumber == chunkNumber;
    }
    public boolean equals(FileChunk fileChunk) {
        return this.fileId.equals(fileChunk.fileId) && this.chunkNumber == fileChunk.chunkNumber;
    }
    public boolean isFromFile(byte[] fileId) {
        return this.fileId.equals(fileId);
    }
}
