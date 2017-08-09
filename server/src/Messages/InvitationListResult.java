package Messages;

import Logic.Client;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;

public class InvitationListResult extends Message{
    Set<Client> list;
    public InvitationListResult(Set<Client> list){
        this.list = list;
    }
    public InvitationListResult(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize() {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1+arrayOutputStream.size());
        buffer.put(MessageTypes.INVITATION_LIST_RESULT);
        buffer.put(arrayOutputStream.toByteArray());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(array , 1 , array.length-1);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
            list = (Set<Client>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getType() {
        return MessageTypes.INVITATION_LIST_RESULT;
    }
    public Set<Client> getList(){
        return list;
    }
}
