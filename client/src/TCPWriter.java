import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPWriter extends Thread{
    SocketChannel connection;
    public TCPWriter(SocketChannel connection){
        this.connection = connection;
        start();
    }
    @Override
    public void run(){
        while(true){
            if(!NetworkHandler.toSendMessages.isEmpty()){
                while(!NetworkHandler.toSendMessages.isEmpty()){
                    ByteBuffer buffer = ByteBuffer.wrap(NetworkHandler.toSendMessages.poll());
                    try {
                        connection.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
