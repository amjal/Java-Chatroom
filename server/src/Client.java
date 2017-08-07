import java.net.SocketAddress;

public class Client {
    protected String id;
    protected SocketAddress address;
    public Client(String id , SocketAddress address){
        this.address = address;
        this.id = id;
    }
    public Client(SocketAddress address){
        this(null , address);
    }
}
