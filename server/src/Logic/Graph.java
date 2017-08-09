package Logic;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Graph {
    private LinkedHashSet<Client> clients;
    private LinkedHashSet<AbstractMap.SimpleEntry<Client,Client>> links;
    public Graph(LinkedHashSet<Client> clients , LinkedHashSet<AbstractMap.SimpleEntry<Client , Client>> links){
        this.clients = clients;
        this.links = links;
    }
    public Graph(LinkedHashSet<Client> clients){
        this.clients = clients;
        links = new LinkedHashSet<>();
    }
    public Graph(){
        clients = new LinkedHashSet<>();
        links = new LinkedHashSet<>();
    }
    public boolean linkExists(Client c1 , Client c2){
        AbstractMap.SimpleEntry<Client,Client> pair = new AbstractMap.SimpleEntry<>(c1 , c2);
        return links.contains(pair);
    }
    public void addClient(Client client){
        clients.add(client);
    }
    public void removeClient(Client client){
        clients.remove(client);
        Iterator<AbstractMap.SimpleEntry<Client, Client>> iterator = links.iterator();
        while(iterator.hasNext()){
            AbstractMap.SimpleEntry<Client , Client> pair = iterator.next();
            if(pair.getValue().equals(client) || pair.getKey().equals(client)){
                links.remove(pair);
            }
        }
    }
    public void addLink(Client c1 , Client c2){
        AbstractMap.SimpleEntry<Client , Client> pair = new AbstractMap.SimpleEntry<>(c1 , c2);
        links.add(pair);
    }
    public void removeLink(Client c1 , Client c2){
        AbstractMap.SimpleEntry<Client , Client> pair = new AbstractMap.SimpleEntry<>(c1 , c2);
        links.remove(pair);
    }
    public boolean areChatting(Client c1 , Client c2){
        AbstractMap.SimpleEntry<Client , Client> pair1= new AbstractMap.SimpleEntry<>(c1 , c2);
        AbstractMap.SimpleEntry<Client , Client> pair2= new AbstractMap.SimpleEntry<>(c2 , c1);
        if(links.contains(pair1) && links.contains(pair2))
            return true;
        return false;
    }
    public boolean isWaitingFor(Client c1 , Client c2){
        AbstractMap.SimpleEntry<Client , Client> pair1= new AbstractMap.SimpleEntry<>(c1 , c2);
        AbstractMap.SimpleEntry<Client , Client> pair2= new AbstractMap.SimpleEntry<>(c2 , c1);
        if(links.contains(pair1) && !links.contains(pair2))
            return true;
        return false;
    }
    public LinkedHashSet<Client> chatBuddies(Client client){
        LinkedHashSet<Client> output = new LinkedHashSet<>();
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()){
            Client c = iterator.next();
            if(areChatting(client , c)){
                output.add(c);
            }
        }
        return output;
    }

    public LinkedHashSet<Client> inviters(Client client){
        LinkedHashSet<Client> output = new LinkedHashSet<>();
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()){
            Client c = iterator.next();
            if(isWaitingFor(c , client)){
                output.add(c);
            }
        }
        return output;
    }
    public LinkedHashSet<Client> invitedBy(Client client){
        LinkedHashSet<Client> output = new LinkedHashSet<>();
        Iterator<Client> iterator = clients.iterator();
        while(iterator.hasNext()){
            Client c  = iterator.next();
            if(isWaitingFor(client , c)){
                output.add(c);
            }
        }
        return output;
    }
}
