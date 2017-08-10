package Logic;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TCPWriter extends TCPCommunicator implements Runnable{
    public TCPWriter(){
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run(){
        while(true){
            threadEnterPointChecker();
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
                thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
