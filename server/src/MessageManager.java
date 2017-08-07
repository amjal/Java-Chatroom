import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class MessageManager extends Thread{
    public MessageManager(){
       start();
    }
    @Override
    public void run(){
        while(true){
            Set<Client> clients = NetworkHandler.receivedMessages.keySet();
            Iterator<Client> iterator = clients.iterator();
            while (iterator.hasNext()){
                Client client = iterator.next();
                byte[] message = NetworkHandler.receivedMessages.get(client);
                iterator.remove();
                switch (message[0]){
                    case MessageTypes.LOGIN:
                        LoginMessage loginMessage = new LoginMessage(message);
                        loginMessage.deserialize();
                        if(NetworkHandler.IDTable.containsValue(loginMessage.ID))
                        {
                            ServerMessage err = new ServerMessage("***ID ALREADY LOGGED IN!***");
                            err.serialize();
                            NetworkHandler.toSendMessages.put(client , err.array);
                            NetworkHandler.addressTable.remove(client.address);
                        }
                        else{
                            NetworkHandler.IDTable.put(loginMessage.ID , client);
                            ServerMessage success = new ServerMessage("***SUCCESSFULLY LOGGED IN***");
                            success.serialize();
                            NetworkHandler.toSendMessages.put(client , success.array);
                        }
                        break;
                }
            }
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
