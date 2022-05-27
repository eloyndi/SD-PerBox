package pt.ipb.dsys.peer;

import org.jgroups.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipb.dsys.peer.box.Repository;
import pt.ipb.dsys.peer.serializable.FileChunk;
import pt.ipb.dsys.peer.serializable.FileID;
import pt.ipb.dsys.peer.serializable.MessageChunkRequest;
import pt.ipb.dsys.peer.serializable.MessageDeleteFile;

public class MessageHandler implements Receiver {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final JChannel channel;
    private final Repository repository;

    public MessageHandler(JChannel channel,  Repository repository) {
        this.channel = channel;
        this.repository = repository;
        repository.setMessageHandler(this);
    }


    @Override
    public void viewAccepted(View view) { }

    @Override
    public void receive(Message msg) {
        if (msg.getObject() instanceof FileID) {
            FileID fileID = msg.getObject();
            repository.AddFile(fileID);
        }
        else if (msg.getObject() instanceof FileChunk) {
            FileChunk fileChunk = msg.getObject();
            repository.AddChunk(fileChunk);
        }
        else if (msg.getObject() instanceof MessageChunkRequest) {
            MessageChunkRequest request = msg.getObject();
            FileChunk chunk = repository.FindChunk(request.fileId, request.nChunk);
            sendTo(request.address, chunk);
        }
        else if (msg.getObject() instanceof MessageDeleteFile) {
            MessageDeleteFile message = msg.getObject();
            repository.RemoveFile(message.pathname);
        }
    }


    public void broadcast(Object obj) {
        try {
            channel.send(new ObjectMessage(null, obj));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendTo(Address member, Object obj) {
        try {
            channel.send(new ObjectMessage(member, obj));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public JChannel getChannel() { return channel; }
    public Repository getRepository() { return repository; }
}
