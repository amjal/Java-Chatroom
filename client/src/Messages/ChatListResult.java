package Messages;


import Logic.Client;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Set;

public class ChatListResult extends Message {
    Set<Client> chatList;
    public ChatListResult(Set<Client> chatList) {
        this.chatList = chatList;
    }
    public ChatListResult(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize(){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(chatList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(1+arrayOutputStream.size());
        buffer.put(MessageTypes.CHAT_LIST_RESULT);
        buffer.put(arrayOutputStream.toByteArray());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(array , 1 , array.length -1);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
            this.chatList = (Set<Client>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getType() {
        return MessageTypes.CHAT_LIST_RESULT;
    }

    public Set<Client> getChatList(){
        return chatList;
    }
}
