package Logic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class NetworkHandler {
    protected static Graph chatNetwork;
    protected static Hashtable<String , Client> IDTable;
    protected static Hashtable<SocketAddress , Client> addressTable;
    private  static LinkedHashMap<Client , byte[]> receivedMessages;
    private static LinkedHashMap<Client , byte[]> toSendMessages;
    protected TCPWriter tcpWriter;
    protected TCPReader tcpReader;
    protected MessageManager messageManager;
    protected ConnectionAccepter connectionAccepter;
    public NetworkHandler(InetSocketAddress address){
        chatNetwork = new Graph();
        IDTable = new Hashtable<>();
        addressTable = new Hashtable<>();
        receivedMessages = new LinkedHashMap<>();
        toSendMessages = new LinkedHashMap<>();
        connectionAccepter = new ConnectionAccepter(address);
        tcpReader = new TCPReader();
        tcpWriter = new TCPWriter();
        connectionAccepter.addConnectionReceptionListener(tcpReader);
        connectionAccepter.addConnectionReceptionListener(tcpWriter);
        messageManager = new MessageManager();
    }
    public void end(){
        try {
            tcpWriter.join();
            tcpReader.join();
            messageManager.join();
            connectionAccepter.join();
            connectionAccepter.serverSocketChannel.close();
            connectionAccepter.selector.close();
            tcpWriter.selector.close();
            tcpReader.selector.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
