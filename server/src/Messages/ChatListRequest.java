package Messages;

public class ChatListRequest extends Message {
    @Override
    public void serialize() {
        array = new byte[1];
        array[0] = getType();
    }

    @Override
    public void deserialize() {
    }

    @Override
    public byte getType() {
        return MessageTypes.CHAT_LIST_REQUEST;
    }
}
