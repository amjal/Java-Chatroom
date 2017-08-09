package Logic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NetworkHandler {
    protected static ArrayDeque<byte[]> receivedMessages;
    protected static ArrayDeque<byte[]> toSendMessages;
    SocketChannel connection =null;
    public NetworkHandler(InetSocketAddress address){
        receivedMessages = new ArrayDeque<>();
        toSendMessages = new ArrayDeque<>();
        Scanner sc = new Scanner(System.in);
        try {
            connection = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!connection.isConnected()) {
            System.out.println("***TYPE IN \"START\" TO TRY TO CONNECT TO SERVER(CASE INSENSITIVE)***");
            Pattern pattern = Pattern.compile("start",Pattern.CASE_INSENSITIVE);
            String input = sc.nextLine();
            if(pattern.matcher(input).matches()) {
                try {
                    connection.connect(address);
                } catch (IOException e) {
                    System.out.println("***CONNECTION REFUSED***");
                }
            }
        }
        System.out.println("***SUCCESSFULLY CONNECTED TO SERVER***");
        new TCPReader(connection);
        new TCPWriter(connection);
    }
}
