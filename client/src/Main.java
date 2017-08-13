
import Logic.NetworkHandler;
import Logic.UserHandler;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
            new NetworkHandler(address);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("***BE SURE TO ENTER THE IP ADDRESS AS WELL AS PORT NUMBER WHEN YOU ARE OPENNING " +
                    "THE APPLICATION***");
        }
    }
}
