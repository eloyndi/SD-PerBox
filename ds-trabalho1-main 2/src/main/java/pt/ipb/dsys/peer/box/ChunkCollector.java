package pt.ipb.dsys.peer.box;

import org.jgroups.Address;
import pt.ipb.dsys.peer.serializable.FileChunk;
import pt.ipb.dsys.peer.serializable.FileID;
import pt.ipb.dsys.peer.serializable.MessageChunkRequest;

import java.io.IOException;
import java.util.List;

public class ChunkCollector implements Runnable {
    private final Repository repository;
    private final FileID fileID;

    public ChunkCollector(Repository repository, FileID fileID) {
        this.repository = repository;
        this.fileID = fileID;
    }

    @Override
    public void run() {
        int nChunks = fileID.getNChunks();
        byte[] id = fileID.getFileId();
        FileChunk[] fileChunks = new FileChunk[nChunks];

        //Tenta obter chunks
        for (int i = 0; i < nChunks; i++) {
            List<Address> members = repository.getMessageHandler().getChannel().getView().getMembers();
            Address host = repository.getMessageHandler().getChannel().getAddress();

            FileChunk chunk = repository.FindChunk(id, i);
            if(chunk == null) {
                MessageChunkRequest message = new MessageChunkRequest(repository.getMessageHandler().getChannel().getAddress(), id, i);
                for (Address a: members) {
                    if(a == host) continue;

                    sleep(100);

                    chunk = repository.FindChunk(id, i);
                    if(chunk == null) {
                        continue;
                    }
                    else {
                        fileChunks[i] = chunk;
                        break;
                    }
                }

                ThrowException(
                        "ERRO: Falha ao baixar o ficheiro " + fileID.getFilePathname() + "!\n" +
                        "Chunks insuficientes.\n" +
                        "Tente novamente.\n");
                return;
            }
            else {
                fileChunks[i] = chunk;
            }
        }

        //Junta os bytes
        int fileSize = 0;
        for (FileChunk chunk : fileChunks) {
            fileSize += chunk.getContent().length;
        }
        byte[] bytes = new byte[fileSize];

        int i = 0;
        for (FileChunk chunk : fileChunks) {
            for (byte b : chunk.getContent()) {
                bytes[i] = b;
                i++;
            }
        }

        //Escreve ficheiro
        try {
            FileHandler.WriteFileFromBytes(bytes, fileID.getFilePathname());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) { }
    }

    private void ThrowException(String message) {
        try {
            throw new CustomException(message);
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }
}
