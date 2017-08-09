package Messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class AcceptMessage extends Message {
    String ID;
    public AcceptMessage(String ID){
        this.ID = ID;
    }
    public AcceptMessage(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize() {
        int size = 1+ID.length();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(MessageTypes.ACCEPT_MESSAGE);
        buffer.put(ID.getBytes());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        ID = new String(Arrays.copyOfRange(array,1,array.length));
    }

    @Override
    public byte getType() {
        return MessageTypes.ACCEPT_MESSAGE;
    }
    public String getID(){
        return ID;
    }
}
