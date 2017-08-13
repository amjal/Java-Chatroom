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
        inputHandler();
    }
    public void inputHandler(){
        Scanner sc = new Scanner(System.in);
        String input;
        do{
            input = sc.nextLine();
            if(input.equals("quit")) {
                end();
                return;
            }
        }while (true);
    }
    public void end(){
        try {
            connectionAccepter.serverSocketChannel.close();
            connectionAccepter.selector.wakeup();
            connectionAccepter.kill();
            connectionAccepter.join();
            connectionAccepter.selector.close();
            tcpWriter.kill();
            tcpReader.kill();
            messageManager.kill();
            tcpWriter.join();
            tcpReader.join();
            tcpWriter.selector.close();
            tcpReader.selector.close();
            System.out.println("***CLOSED EVERYTHING***");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
