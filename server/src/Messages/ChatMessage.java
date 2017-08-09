package Messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ChatMessage extends Message {
    String ID;
    String message;
    public ChatMessage(String ID , String message){
        this.ID = ID;
        this.message = message;
    }
    public ChatMessage(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize() {
        int size = 1+4+ID.length()+message.length();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(MessageTypes.CHAT_MESSAGE);
        buffer.putInt(ID.length());
        buffer.put(ID.getBytes());
        buffer.put(message.getBytes());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        int l = ByteBuffer.wrap(Arrays.copyOfRange(array , 1 , 5)).getInt();
        ID = new String(Arrays.copyOfRange(array , 5 , 5+l));
        message = new String(Arrays.copyOfRange(array,5+l,array.length));
    }

    @Override
    public byte getType() {
        return MessageTypes.CHAT_MESSAGE;
    }
    @Override
    public String toString(){
        return message;
    }
    public String getID(){
        return ID;
    }
    public void setID(String ID){
        this.ID = ID;
    }
}
