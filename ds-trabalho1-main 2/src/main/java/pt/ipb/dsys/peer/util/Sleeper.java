package pt.ipb.dsys.peer.util;

public interface Sleeper {

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
            // don't care
        }
    }

}
