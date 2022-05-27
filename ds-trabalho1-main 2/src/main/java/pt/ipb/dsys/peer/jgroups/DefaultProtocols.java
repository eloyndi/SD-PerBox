package pt.ipb.dsys.peer.jgroups;

import com.google.common.collect.Lists;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.TUNNEL;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.stack.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DefaultProtocols {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProtocols.class);

    public static List<Protocol> gossipRouter(String gossipHostname, int gossipPort) throws UnknownHostException {
        List<Protocol> protocols = Lists.newArrayList();

        TUNNEL tunnel = new TUNNEL();
        try {
            InetAddress grAddress = InetAddress.getByName(gossipHostname);
            logger.info("Found gossip router at {} (using it)", grAddress);
            tunnel.setGossipRouterHosts(String.format("%s[%d]", gossipHostname, gossipPort));
        } catch (UnknownHostException e) {
            logger.info("Couldn't find address for '{}' falling back to 127.0.0.1", gossipHostname);
            tunnel.setGossipRouterHosts(String.format("127.0.0.1[%d]", gossipPort));
        }

        protocols.add(tunnel);
        protocols.add(new PING());
        protocols.add(new NAKACK2());
        protocols.add(new GMS());

//        protocols.add(new MERGE3());
//        protocols.add(new FD_SOCK());
//        protocols.add(new FD_ALL3());
//        protocols.add(new VERIFY_SUSPECT());
//        protocols.add(new UNICAST3());
//        protocols.add(new STABLE());
//        protocols.add(new MFC());
//        protocols.add(new FRAG2());
//        protocols.add(new BARRIER());
//        protocols.add(new STATE_TRANSFER());

        return protocols;
    }

}
