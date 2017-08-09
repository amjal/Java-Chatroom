
import Logic.NetworkHandler;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
        new NetworkHandler(address);
    }
}

