package Logic;

import java.nio.channels.SocketChannel;

public class TCPCommunicator{
    Thread thread;
    boolean go = false;
    SocketChannel connection;
    public void suspend(){
        go = false;
    }
    synchronized public void resume(SocketChannel connection){
        this.connection = connection;
        go = true;
        notify();
    }
    synchronized public void threadEnterPointChecker(){
        if (!go)
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
