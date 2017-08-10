package Logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.Arrays;

public class TCPReader extends TCPCommunicator implements Runnable{
    ByteBuffer buffer;
    public TCPReader(){
        buffer = ByteBuffer.allocate(1024);
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run(){
        while (true){
            threadEnterPointChecker();
            try {
                connection.read(buffer);
                buffer.flip();
                byte[] temp = Arrays.copyOfRange(buffer.array() , buffer.position() , buffer.limit());
                NetworkHandler.receivedMessages.add(temp);
                buffer.clear();
            }catch(AsynchronousCloseException e) {
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
