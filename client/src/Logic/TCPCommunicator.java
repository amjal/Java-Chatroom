package Logic;

import java.nio.channels.SocketChannel;

/**
 * This class provides the fundamental tasks needed by TCPReader and TCPWriter. including their
 * suspension and resuming of the threads. The mechanism is straight forward. At the beginning of
 * each cycle, the threads check with a boolean to see if they are allowed to keep going or
 * should suspend. if the flag is false, they enter their embedding objects' monitor, where method
 * wait() is called. So the thread is put to sleep. The process of waking them up is done in the
 * NetworkHandler class and by the main thread. Which accesses the object's monitors and calls
 * notify() in them. also sets the flag to true which means the threads are going to keep going
 * without being suspended until the next time the flag is set to false
 */
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
