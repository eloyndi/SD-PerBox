package pt.ipb.dsys.peer.serializable;

import java.io.Serializable;

public class MessageDeleteFile implements Serializable {
    public final String pathname;

    public MessageDeleteFile(String pathname) {
        this.pathname = pathname;
    }
}
