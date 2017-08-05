import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String message;
        InetSocketAddress address = new InetSocketAddress(args[0] , Integer.parseInt(args[1]));
        Scanner sc = new Scanner(System.in);
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(address);
            ByteBuffer buffer = ByteBuffer.allocate(256);
            do{
                message = sc.next();
                buffer.put(message.getBytes());
                buffer.flip();
                channel.write(buffer);
                buffer.clear();
                channel.read(buffer);
                message = new String(buffer.array());
            }while(message.equals("quit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
