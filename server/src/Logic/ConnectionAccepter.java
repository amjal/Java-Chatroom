package Logic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class's sole purpose is to accept new connections to the server. It registers a non-blocking
 * ServerSocketChannel with a selector with only the accept operation. The rest is easy to figure out if your are
 * familiar with NIO. This class holds a list of listeners. These listeners are basically our TCPReader and TCPWriter
 * objects. They are listening for arrival of new connections because they should register their own selectors with
 * the arriving connections.
 */
class ConnectionAccepter extends Thread{
    protected ServerSocketChannel serverSocketChannel;
    protected Selector selector;
    protected List<ConnectionReceptionListener> connectionReceptionListeners;
    boolean go = true;
    public ConnectionAccepter(InetSocketAddress address){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
            serverSocketChannel.register(selector , SelectionKey.OP_ACCEPT);
            connectionReceptionListeners = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("***SUCCESSFULLY CONFIGURED SERVER***");
        start();
    }
    public void kill(){
        go = false;
    }
    @Override
    public void run(){
        while(go){
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                try {
                    SocketChannel connection = ((ServerSocketChannel) key.channel()).accept();
                    connection.configureBlocking(false);
                    SocketAddress address = connection.getRemoteAddress();
                    NetworkHandler.addressTable.put(address, new Client(address));
                    for (ConnectionReceptionListener crl : connectionReceptionListeners) {
                        crl.onNewConnectionReceived(connection);
                    }
                    System.out.println("***ONLINE CLIENTS: " + NetworkHandler.addressTable.size() + " ***");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addConnectionReceptionListener(ConnectionReceptionListener crl){
       connectionReceptionListeners.add(crl);
    }
}
