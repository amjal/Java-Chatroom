import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.*;

public class NetworkHandler {
    //TODO a proper data structure for waiting list
    protected static Hashtable<String , Client> IDTable;
    protected static Hashtable<SocketAddress , Client> addressTable;
    protected static LinkedHashMap<Client , byte[]> receivedMessages;
    protected static LinkedHashMap<Client , byte[]> toSendMessages;
    protected static List<AbstractMap.SimpleEntry<Client , Client>> chatPairs;
    protected HashMap<String , String> test;
    public NetworkHandler(InetSocketAddress address){
        IDTable = new Hashtable<>();
        addressTable = new Hashtable<>();
        chatPairs = new ArrayList<>();
        receivedMessages = new LinkedHashMap<>();
        toSendMessages = new LinkedHashMap<>();
        ConnectionAccepter connectionAccepter = new ConnectionAccepter(address);
        connectionAccepter.addConnectionReceptionListener(new TCPReader());
        connectionAccepter.addConnectionReceptionListener(new TCPWriter());
        new MessageManager();
    }
}
