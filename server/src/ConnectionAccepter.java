import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class ConnectionAccepter extends Thread{
    protected ServerSocketChannel serverSocketChannel;
    protected Selector selector;
    protected List<ConnectionReceptionListener> connectionReceptionListeners;
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
        start();
    }
    @Override
    public void run(){
        while(true){
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                try {
                    SocketChannel connection = ((ServerSocketChannel)key.channel()).accept();
                    connection.configureBlocking(false);
                    SocketAddress address = connection.getRemoteAddress();
                    NetworkHandler.addressTable.put(address , new Client(address));
                    for(ConnectionReceptionListener crl:connectionReceptionListeners){
                        crl.onNewConnectionReceived(connection);
                    }
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
