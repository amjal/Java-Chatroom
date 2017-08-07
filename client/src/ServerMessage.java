import java.nio.ByteBuffer;
import java.util.Arrays;

public class ServerMessage extends Message {
    protected String message;
    public ServerMessage(String message){
        this.message = message;
    }
    public ServerMessage(byte[] array){
        this.array = array;
    }
    @Override
    public void serialize() {
        int size = 1+message.length();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(getType());
        buffer.put(message.getBytes());
        array = buffer.array();
    }

    @Override
    public void deserialize() {
        byte[] messageData = Arrays.copyOfRange(array, 1 , array.length);
        message = new String(messageData);
    }

    @Override
    public byte getType() {
        return MessageTypes.SERVER_MESSAGE;
    }
    @Override
    public String toString(){
        return message;
    }
}
