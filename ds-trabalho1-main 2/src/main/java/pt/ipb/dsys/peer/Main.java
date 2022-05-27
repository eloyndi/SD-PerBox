package pt.ipb.dsys.peer;

import org.jgroups.JChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipb.dsys.peer.box.Repository;
import pt.ipb.dsys.peer.jgroups.DefaultProtocols;
import pt.ipb.dsys.peer.jgroups.PeerUtil;

import javax.swing.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static final String CLUSTER_NAME = "PeerBox";

    public static final String GOSSIP_HOSTNAME = "gossip-router"; // see container_name on docker-compose.yml

    public static final int GOSSIP_PORT = 12001;


    public static void main(String[] args) {
        PeerUtil.localhostFix(GOSSIP_HOSTNAME);
        try (JChannel channel = new JChannel(DefaultProtocols.gossipRouter(GOSSIP_HOSTNAME, GOSSIP_PORT))) {

            Repository repository= new Repository();
            MessageHandler messageHandler = new MessageHandler(channel, repository);

            channel
                    // ignore my own messages
                    .setDiscardOwnMessages(false)
                    // receiver must be set first for Receiver.viewAccepted(...) to work properly
                    .setReceiver(messageHandler)
                    // finaly connect to the cluster
                    .connect(CLUSTER_NAME)
            ;

            JFrame jFrame = new JFrame("DS-Trabalho1");
            UI_Main ui = new UI_Main(repository, jFrame);
            jFrame.setContentPane(ui.root);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.pack();
            jFrame.setSize(1024,720);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        System.exit(0);
    }
}
