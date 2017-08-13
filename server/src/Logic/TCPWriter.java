package Logic;

import Messages.MessageTypes;

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
    boolean go = true;
    public TCPWriter(){
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }
    public void kill(){
        go = false;
    }
    @Override
    public void run(){
        while(go){
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
                    if(NetworkHandler.toSendMessagesContainsKey(client)){
                        byte[] message = NetworkHandler.toSendMessagesGet(client);
                        buffer.put(message);
                        buffer.flip();
                        connection.write(buffer);
                        buffer.clear();
                    }
                    NetworkHandler.toSendMessagesRemove(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(100);
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
