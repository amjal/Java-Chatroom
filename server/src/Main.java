
import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        String message;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
            serverSocketChannel.register(selector , SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    serverSocketChannel = (ServerSocketChannel) key.channel();
                    try {
                        SocketChannel connection = serverSocketChannel.accept();
                        connection.configureBlocking(false);
                        SelectionKey connectionKey = connection.register(selector , SelectionKey.OP_READ);
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        connectionKey.attach(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if (key.isReadable()){
                    SocketChannel connection = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    try {
                        connection.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    buffer.flip();
                    byte[] temp = Arrays.copyOfRange(buffer.array() , buffer.position() , buffer.limit());
                    message = new String(temp);
                    if(message.equals("quit")){
                        try {
                            key.cancel();
                            connection.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(message);
                    buffer.clear();
                }
            }
        }
    }
}
