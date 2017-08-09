package Logic;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class NetworkHandler {
    protected static Graph chatNetwork;
    protected static Hashtable<String , Client> IDTable;
    protected static Hashtable<SocketAddress , Client> addressTable;
    private  static LinkedHashMap<Client , byte[]> receivedMessages;
    private static LinkedHashMap<Client , byte[]> toSendMessages;
    public NetworkHandler(InetSocketAddress address){
        chatNetwork = new Graph();
        IDTable = new Hashtable<>();
        addressTable = new Hashtable<>();
        receivedMessages = new LinkedHashMap<>();
        toSendMessages = new LinkedHashMap<>();
        ConnectionAccepter connectionAccepter = new ConnectionAccepter(address);
        connectionAccepter.addConnectionReceptionListener(new TCPReader());
        connectionAccepter.addConnectionReceptionListener(new TCPWriter());
        new MessageManager();
    }
    public static synchronized boolean toSendMessagesContainsKey(Client client){
        return toSendMessages.containsKey(client);
    }
    public static synchronized byte[] toSendMessagesGet(Client client){
        return toSendMessages.get(client);
    }
    public static synchronized void toSendMessagesRemove(Client client){
        toSendMessages.remove(client);
    }
    public static synchronized void receivedMessagesPut(Client client, byte[] data){
        receivedMessages.put(client , data);
    }
    public static synchronized Set<Client> receivedMessagesKeySet(){
        return receivedMessages.keySet();
    }
    public static synchronized byte[] receivedMessagesGet(Client client){
        return receivedMessages.get(client);
    }
    public static synchronized void toSendMessagesPut(Client client , byte[] data){
        toSendMessages.put(client , data);
    }
}
