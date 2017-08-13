package Logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;

public class TCPReader extends TCPCommunicator implements Runnable{
    ByteBuffer buffer;
    ServiceStopListener ssl;
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
                int opt = connection.read(buffer);
                if(opt == -1){
                    System.out.println("***CONNECTION LOST***");
                    ssl.notConnectedStage();
                }
                else {
                    buffer.flip();
                    byte[] temp = Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit());
                    NetworkHandler.receivedMessages.add(temp);
                    buffer.clear();
                }
            }catch(AsynchronousCloseException e) {
            }catch(ClosedChannelException e) {
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void addServiceStopListener(ServiceStopListener ssl){
        this.ssl = ssl;
    }
}
