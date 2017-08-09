package Messages;

public abstract class Message {
    protected byte[] array;
    public abstract void serialize();
    public abstract void deserialize();
    public abstract byte getType();

    public byte[] getArray() {
        return array;
    }
}
