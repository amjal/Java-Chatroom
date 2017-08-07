import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class TCPReader extends Thread implements ConnectionReceptionListener{
    Selector selector;
    public TCPReader(){
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
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                SocketChannel connection = (SocketChannel) key.channel();
                try {
                    connection.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer.flip();
                byte[] temp = Arrays.copyOfRange(buffer.array() , buffer.position() , buffer.limit());
                try {
                    SocketAddress address = connection.getRemoteAddress();
                    NetworkHandler.receivedMessages.put(NetworkHandler.addressTable.get(address) , temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer.clear();
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
            SelectionKey connectionKey = connection.register(selector , SelectionKey.OP_READ);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            connectionKey.attach(buffer);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
