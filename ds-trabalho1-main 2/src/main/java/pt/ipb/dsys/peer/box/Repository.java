package pt.ipb.dsys.peer.box;

import org.jgroups.Address;
import pt.ipb.dsys.peer.MessageHandler;
import pt.ipb.dsys.peer.serializable.FileChunk;
import pt.ipb.dsys.peer.serializable.FileID;
import pt.ipb.dsys.peer.serializable.MessageDeleteFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Repository {
    public static final int CHUNKSIZE  = 64 * 1024;

    private MessageHandler messageHandler;
    private int currentSendedAddress = 0;
    private ArrayList<FileID> files;
    private ArrayList<FileChunk> chunks;


    public Repository() {
        chunks = new ArrayList<>();
        files = new ArrayList<>();
    }


    public void SaveFile(String pathname, int replicas) {
        //Gera ID
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] id = md.digest(pathname.getBytes());


        //Obtem conteudo em bytes
        byte[] bytes = new byte[0];
        try {
            bytes = FileHandler.GetFileBytes(pathname);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        //Numero de chunks
        int nChunks = bytes.length / CHUNKSIZE;
        if(bytes.length % CHUNKSIZE > 0)
            nChunks ++;


        //FileID
        FileID fileID = new FileID(id, pathname, nChunks);
        messageHandler.broadcast(fileID);


        //Separa Chunks
        ArrayList<FileChunk> chunks = new ArrayList<>();
        byte[] byteChunk = new byte[nextChunkSize(bytes.length, 0)];
        int i = 0;      //index no chunk
        int ic = 0;     //index do chunk
        for (byte b: bytes) {
            byteChunk[i] = b;
            i++;
            if(i == CHUNKSIZE) {
                chunks.add(new FileChunk(id, ic, byteChunk));
                i = 0;
                ic++;
                byteChunk = new byte[nextChunkSize(bytes.length, ic)];
            }
        }
        if(i < CHUNKSIZE) {
            chunks.add(new FileChunk(id, ic, byteChunk));
        }


        //Distribui os chunks
        Address[] addresses = messageHandler.getChannel().getView().getMembersRaw();

        Hashtable<Address, ArrayList<FileChunk>> addressChunks = new Hashtable<>();
        for (Address a: addresses) {
            addressChunks.put(a, new ArrayList<>());
        }

        if(replicas > addresses.length)
        {
            replicas = addresses.length;
            System.out.printf("AVIZO!!! : Número de replicas superior a número de endereços.\nO numero de replicas será %d", replicas);
        }

        for (i = 0; i < replicas; i++) {
            for (ic = 0; ic < nChunks;) {
                ArrayList<FileChunk> chunkList = addressChunks.get(addresses[currentSendedAddress]);
                FileChunk chunk = chunks.get(ic);

                if(!chunkList.contains(chunk)) {
                    chunkList.add(chunk);
                    ic++;
                }
                nextAddress(addresses.length);
            }
        }


        //Envia os chunks
        for (Address a: addresses) {
            ArrayList<FileChunk> chunkList = addressChunks.get(a);

            Address host = messageHandler.getChannel().getAddress();
            if (a == host) {
                for (FileChunk f : chunkList) {
                    AddChunk(f);
                }
            }
            else {
                for (FileChunk f : chunkList) {
                    messageHandler.sendTo(a, f);
                }
            }
        }
    }

    public void DeleteFile(String pathname) {
        MessageDeleteFile message = new MessageDeleteFile(pathname);
        messageHandler.broadcast(message);
    }

    public void DownloadFile(String pathname) {
        FileID fileID = FindFile(pathname);
        if(fileID == null) {
            ThrowException("ERRO: Ficheiro não existe.\n");
        }

        new Thread(new ChunkCollector(this, fileID)).start();
    }

    public void UpdateFile(String pathname, int replicas) {
        DeleteFile(pathname);
        SaveFile(pathname, replicas);
    }


    public synchronized void AddFile(FileID file) {
        if (ContainsFile(file)) return;
        files.add(file);
    }
    public synchronized void AddChunk(FileChunk chunk) {
        if(ContainsChunk(chunk)) return;
        chunks.add(chunk);
    }

    public synchronized void RemoveFile(String pathname) {
        FileID file = FindFile(pathname);

        int i;
        for (i = chunks.size() - 1; i > -1; i--) {
            if(chunks.get(i).isFromFile(file.getFileId()))
                chunks.remove(i);
        }

        files.remove(file);
    }

    public boolean ContainsFile(FileID fileID) {
        for (FileID f: files) {
            if(f.equals(fileID)) return true;
        }
        return false;
    }
    public boolean ContainsChunk(FileChunk fileChunk) {
        for (FileChunk c: chunks) {
            if (c.equals(fileChunk)) return true;
        }
        return false;
    }

    public FileID FindFile(String pathname) {
        for (FileID f: files) {
            if(f.equals(pathname)) return f;
        }
        return null;
    }
    public FileChunk FindChunk(byte[] fileId, int chunkNumber) {
        for (FileChunk c: chunks) {
            if(c.equals(fileId, chunkNumber)) return c;
        }
        return null;
    }

    public MessageHandler getMessageHandler() { return messageHandler; }
    public void setMessageHandler(MessageHandler messageHandler) { this.messageHandler = messageHandler; }



    private int nextChunkSize(int length, int offset) {
        int remaining = length - offset * CHUNKSIZE;
        if(remaining < CHUNKSIZE) return remaining;
        else return CHUNKSIZE;
    }
    private void nextAddress(int addressesLength) {
        currentSendedAddress++;
        if (currentSendedAddress == addressesLength)
            currentSendedAddress = 0;
    }
    private void ThrowException(String message) {
        try {
            throw new CustomException(message);
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }
}
