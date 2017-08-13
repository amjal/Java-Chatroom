
import Logic.NetworkHandler;

import java.net.*;
import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(Integer.parseInt(args[0]));
            new NetworkHandler(address);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("BE SURE TO ENTER THE PORT NUMBER ON OPENING THE PROGRAM");
            System.exit(0);
        }
    }
}

