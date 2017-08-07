import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

public class NetworkHandler {
    protected static ArrayDeque<byte[]> receivedMessages;
    protected static ArrayDeque<byte[]> toSendMessages;
    SocketChannel connection =null;
    public NetworkHandler(InetSocketAddress address){
        receivedMessages = new ArrayDeque<>();
        toSendMessages = new ArrayDeque<>();
        try {
            connection = SocketChannel.open();
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new TCPReader(connection);
        new TCPWriter(connection);
    }
}
