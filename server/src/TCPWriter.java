import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPWriter extends Thread implements ConnectionReceptionListener{
    Selector selector;
    MessageManager messageManager;
    public TCPWriter(){
        messageManager = new MessageManager();
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }
    @Override
    public void run(){
        while(true){
            try {
                selector.selectNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                SocketChannel connection = (SocketChannel) key.channel();
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                try {
                    SocketAddress address = connection.getRemoteAddress();
                    Client client = NetworkHandler.addressTable.get(address);
                    if(NetworkHandler.toSendMessages.containsKey(client)){
                        byte[] message = NetworkHandler.toSendMessages.get(client);
                        buffer.put(message);
                        buffer.flip();
                        connection.write(buffer);
                        buffer.clear();
                    }
                    NetworkHandler.toSendMessages.remove(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewConnectionReceived(SocketChannel connection) {
        try {
            SelectionKey connectionKey = connection.register(selector , SelectionKey.OP_WRITE);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            connectionKey.attach(buffer);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
