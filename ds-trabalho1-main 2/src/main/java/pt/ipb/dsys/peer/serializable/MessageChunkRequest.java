package pt.ipb.dsys.peer.serializable;

import org.jgroups.Address;

import java.io.Serializable;

public class MessageChunkRequest implements Serializable {
    public final Address address;
    public final byte[] fileId;
    public final int nChunk;

    public MessageChunkRequest(Address address, byte[] fileId, int nChunk) {
        this.address = address;
        this.fileId = fileId;
        this.nChunk = nChunk;
    }
}
