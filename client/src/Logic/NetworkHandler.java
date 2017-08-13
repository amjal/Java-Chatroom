package Logic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.regex.Pattern;

public class NetworkHandler implements ServiceStopListener{
    protected static ArrayDeque<byte[]> receivedMessages;
    protected static ArrayDeque<byte[]> toSendMessages;
    SocketChannel connection =null;
    InetSocketAddress address;
    TCPReader tcpReader;
    TCPWriter tcpWriter;
    UserHandler userHandler;
    public NetworkHandler(InetSocketAddress address){
        receivedMessages = new ArrayDeque<>();
        toSendMessages = new ArrayDeque<>();
        this.address = address;
        tcpReader = new TCPReader();
        tcpWriter = new TCPWriter();
        userHandler = new UserHandler();
        userHandler.addServiceStopListener(this);
        tcpReader.addServiceStopListener(this);
        notConnectedStage();
    }

    /**
     * method below is always called where the user must be put in a stage where
     * they are not connected to the server. this method along with the ConsoleReader class
     * completely handle the stage mentioned.
     */
    @Override
    public void tryConnection(String input){
        Pattern startPattern = Pattern.compile("start",Pattern.CASE_INSENSITIVE);
        Pattern quitPattern = Pattern.compile("quit",Pattern.CASE_INSENSITIVE);
        if(startPattern.matcher(input).matches()) {
            try {
                connection = SocketChannel.open();
                connection.connect(address);
            } catch (IOException e) {
                System.out.println("***CONNECTION REFUSED***");
            }
        }
        else if(quitPattern.matcher(input).matches()){
            System.exit(0);
        }
        if(connection != null && connection.isConnected()){
            System.out.println("***SUCCESSFULLY CONNECTED TO SERVER***");
            tcpReader.resume(connection);
            tcpWriter.resume(connection);
            userHandler.resume();
        }
    }

    /**
     * method below basically disconnects the user from the server, it does it by simply
     * closing the connection. Suspending unnecessary threads is essential for every java
     * program and what this method does besides disconnecting is suspending all the threads
     * in the program except for the ConsoleReader thread in UserHandler class and of course
     * the main thread. the details on how the threads are suspended are mentioned in TCPCommunicator
     * class
     */
    @Override
    public void notConnectedStage() {
        try {
            connection.close();
        } catch (IOException e) {
        }catch(NullPointerException e){

        }
        tcpReader.suspend();
        tcpWriter.suspend();
        userHandler.suspend();
    }
}
