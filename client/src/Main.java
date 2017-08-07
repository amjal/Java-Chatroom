import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //TODO  don't let them request chats until they're logged in
        InetSocketAddress address = new InetSocketAddress(args[0] , Integer.parseInt(args[1]));
        new NetworkHandler(address);
        new UserHandler();
    }
}
