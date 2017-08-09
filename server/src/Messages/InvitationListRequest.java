package Messages;

public class InvitationListRequest extends Message {
    int att;
    public InvitationListRequest(int att){
        this.att = att;
    }
    @Override
    public void serialize() {
        array = new byte[1];
        array[0] = getType();
    }

    @Override
    public void deserialize() {
        att = (int) array[0];
    }

    @Override
    public byte getType() {
        switch (att){
            case 0:
                return MessageTypes.I_INVITATION_LIST;
            case 1:
                return MessageTypes.O_INVITATION_LIST;
                default: return -1;
        }
    }
}
