import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class TCPReader extends Thread{
    SocketChannel connection;
    ByteBuffer buffer;
    public TCPReader(SocketChannel connection){
        this.connection = connection;
        buffer = ByteBuffer.allocate(1024);
        start();
    }
    @Override
    public void run(){
        while (true){
            try {
                connection.read(buffer);
                buffer.flip();
                byte[] temp = Arrays.copyOfRange(buffer.array() , buffer.position() , buffer.limit());
                NetworkHandler.receivedMessages.add(temp);
                buffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
