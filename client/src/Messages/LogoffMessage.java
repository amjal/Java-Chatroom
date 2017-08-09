package Messages;

public class LogoffMessage extends Message {
    @Override
    public void serialize() {
        array = new byte[1];
        array[0] = MessageTypes.LOGOFF;
    }

    @Override
    public void deserialize() {
    }

    @Override
    public byte getType() {
        return MessageTypes.LOGOFF;
    }
}
