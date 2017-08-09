package Messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class QuitChatRequest extends Message {
    String ID;
    public QuitChatRequest(String ID){
        this.ID = ID;
    }
    public QuitChatRequest(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize() {
        int size = 1 + ID.length();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(MessageTypes.CHAT_QUIT_MESSAGE);
        buffer.put(ID.getBytes());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        ID = new String(Arrays.copyOfRange(array,1,array.length));
    }

    @Override
    public byte getType() {
        return MessageTypes.CHAT_QUIT_MESSAGE;
    }
    public String getID(){
        return ID;
    }
}
