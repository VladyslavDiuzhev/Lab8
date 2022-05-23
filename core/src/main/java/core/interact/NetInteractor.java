package core.interact;

import java.io.*;

public class NetInteractor implements UserInteractor {
    private final ObjectOutputStream dataOutputStream;
    private final ObjectInputStream dataInputStream;

    public NetInteractor(ObjectInputStream dataInputStream, ObjectOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    public ObjectInputStream getDataInputStream() {
        return dataInputStream;
    }

    public ObjectOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    @Override
    public String getData() {
        try {
            return dataInputStream.readUTF();
        } catch (Exception e) {
            return "";
        }

    }

    @Override
    public void broadcastMessage(String msg, boolean newLine) {
        try {
            if (newLine) {
                dataOutputStream.writeBytes(msg + "\n");
            } else {
                dataOutputStream.writeBytes(msg);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getSecureData() {
        return null;
    }

    public void sendObject(Object obj) throws IOException {
        dataOutputStream.writeObject(obj);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return dataInputStream.readObject();
    }
}
