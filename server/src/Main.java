
import Logic.NetworkHandler;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
        NetworkHandler networkHandler = new NetworkHandler(address);
        networkHandler.end();
    }
}
//TODO client's SocketChannel seems to be sending an empty array on closing. investigate this

