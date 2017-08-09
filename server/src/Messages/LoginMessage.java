package Messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class LoginMessage extends Message {
    String ID;
    public LoginMessage(byte[] array){
        this.array = array;
    }
    public LoginMessage(String ID){
        this.ID = ID;
    }
    @Override
    public void serialize() {
        int size = 1+ID.length();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(getType());
        buffer.put(ID.getBytes());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        ID = new String(Arrays.copyOfRange(array,1,array.length));
    }

    @Override
    public byte getType() {
        return MessageTypes.LOGIN;
    }
    public String getID(){
        return ID;
    }
}
