
import Logic.NetworkHandler;
import Logic.UserHandler;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(args[0] , Integer.parseInt(args[1]));
        NetworkHandler networkHandler = new NetworkHandler(address);
        (new UserHandler()).addServiceStopListener(networkHandler);
    }
}
