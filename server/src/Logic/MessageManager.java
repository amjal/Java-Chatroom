package Logic;

import Messages.*;
import java.util.Iterator;
import java.util.Set;

public class MessageManager extends Thread{
    public MessageManager(){
       start();
    }
    @Override
    public void run(){
        while(true){
            Set<Client> clients = NetworkHandler.receivedMessagesKeySet();
            Iterator<Client> iterator = clients.iterator();
            while (iterator.hasNext()){
                Client client = iterator.next();
                byte[] message = NetworkHandler.receivedMessagesGet(client);
                iterator.remove();
                switch (message[0]){
                    case MessageTypes.LOGIN:
                        LoginMessage loginMessage = new LoginMessage(message);
                        loginMessage.deserialize();
                        if(NetworkHandler.IDTable.containsValue(loginMessage.getID()))
                        {
                            ServerMessage err = new ServerMessage("***ID ALREADY LOGGED IN***");
                            err.serialize();
                            NetworkHandler.toSendMessagesPut(client , err.getArray());
                            NetworkHandler.addressTable.remove(client.address);
                        }
                        else{
                            NetworkHandler.IDTable.put(loginMessage.getID() , client);
                            NetworkHandler.chatNetwork.addClient(client);
                            client.id = loginMessage.getID();
                            ServerMessage success = new ServerMessage("***SUCCESSFULLY LOGGED IN***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
                        }
                        break;
                    case MessageTypes.ACCEPT_MESSAGE:
                        AcceptMessage acceptMessage = new AcceptMessage(message);
                        acceptMessage.deserialize();
                        if(NetworkHandler.IDTable.containsKey(acceptMessage.getID())) {
                            Client acceptedClient = NetworkHandler.IDTable.get(acceptMessage.getID());
                            NetworkHandler.chatNetwork.addLink(client , acceptedClient);
                            ServerMessage success = new ServerMessage("***YOU CAN NOW CHAT WITH "+acceptMessage.getID()+"***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
                            success.setMessage("***"+acceptedClient.id+" HAS ACCEPTED YOUR CHAT REQUEST***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(acceptedClient , success.getArray());
                        }
                        else {
                            ServerMessage err = new ServerMessage("***"+acceptMessage.getID()+" IS OFFLINE***");
                            err.serialize();
                            NetworkHandler.toSendMessagesPut(client , err.getArray());
                        }
                        break;
                    case MessageTypes.CHAT_LIST_REQUEST:
                        Set<Client> chatList = NetworkHandler.chatNetwork.chatBuddies(client);
                        ChatListResult chatListResult = new ChatListResult(chatList);
                        chatListResult.serialize();
                        NetworkHandler.toSendMessagesPut(client , chatListResult.getArray());
                        break;
                    case MessageTypes.CHAT_MESSAGE:
                        ChatMessage chatMessage = new ChatMessage(message);
                        chatMessage.deserialize();
                        Client chatPartner = NetworkHandler.IDTable.get(chatMessage.getID());
                        if(chatPartner == null){
                            ServerMessage err = new ServerMessage("***"+chatMessage.getID()+" IS OFFLINE***");
                            err.serialize();
                            NetworkHandler.toSendMessagesPut(client , err.getArray());
                        }
                        else if(!NetworkHandler.chatNetwork.linkExists(chatPartner , client)) {
                            ServerMessage err = new ServerMessage("***" + chatPartner.id + " IS NOT CHATTING WITH YOU***");
                            err.serialize();
                            NetworkHandler.toSendMessagesPut(client , err.getArray());
                        }
                        else{
                            chatMessage.setID(client.id);
                            chatMessage.serialize();
                            NetworkHandler.toSendMessagesPut(chatPartner , chatMessage.getArray());
                        }
                        break;
                    case MessageTypes.I_INVITATION_LIST:
                        Set<Client> invitedList = NetworkHandler.chatNetwork.invitedBy(client);
                        InvitationListResult invitationListResult = new InvitationListResult(invitedList);
                        invitationListResult.serialize();
                        NetworkHandler.toSendMessagesPut(client , invitationListResult.getArray());
                        break;
                    case MessageTypes.O_INVITATION_LIST:
                        Set<Client> inviterList = NetworkHandler.chatNetwork.inviters(client);
                        invitationListResult = new InvitationListResult(inviterList);
                        invitationListResult.serialize();
                        NetworkHandler.toSendMessagesPut(client , invitationListResult.getArray());
                        break;
                    case MessageTypes.INVITE_MESSAGE:
                        InviteMessage inviteMessage = new InviteMessage(message);
                        inviteMessage.deserialize();
                        Client invited  = NetworkHandler.IDTable.get(inviteMessage.getID());
                        if(invited == null){
                            ServerMessage err = new ServerMessage("***"+inviteMessage.getID()+" IS OFFLINE");
                            err.serialize();
                            NetworkHandler.toSendMessagesPut(client , err.getArray());
                        }
                        else if(NetworkHandler.chatNetwork.linkExists(invited , client)){
                            ServerMessage success = new ServerMessage("***YOU WERE INVITED BY "+
                            invited.id+" BEFORE, YOU'RE CHAT BEGINS NOW***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
                            success = new ServerMessage("***YOU WERE INVITED BY "+
                            client.id+", WHOM YOU HAD INVITED BEFORE, YOU'RE CHAT BEGINS NOW***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(invited , success.getArray());
                        }
                        else{
                            ServerMessage success = new ServerMessage("***INVITATION SENT SUCCESSFULLY***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
                            success = new ServerMessage("***YOU WERE INVITED BY "+
                                    client.id+"***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(invited , success.getArray());
                        }
                        break;
                    case MessageTypes.LOGOFF:
                        NetworkHandler.IDTable.remove(client.id);
                        NetworkHandler.addressTable.remove(client.address);
                        NetworkHandler.chatNetwork.removeClient(client);
                        ServerMessage success = new ServerMessage("***SUCCESSFULLY LOGGED-OFF***");
                        success.serialize();
                        NetworkHandler.toSendMessagesPut(client , success.getArray());
                        break;
                    case MessageTypes.CHAT_QUIT_MESSAGE:
                        QuitChatRequest quitChatRequest = new QuitChatRequest(message);
                        quitChatRequest.deserialize();
                        Client otherSide = NetworkHandler.IDTable.get(quitChatRequest.getID());
                        if(otherSide != null){
                            NetworkHandler.chatNetwork.removeLink(client , otherSide);
                            NetworkHandler.chatNetwork.removeLink(otherSide , client);
                            success = new ServerMessage("***"+ client.id + " HAS STOPPED CHATTING" +
                                    " WITH YOU***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(otherSide , success.getArray());
                            success = new ServerMessage("*** SUCCESSFULLY QUITED CHATTING WITH "+otherSide.id+" ***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
                        }
                        break;
                    case MessageTypes.REJECT_MESSAGE:
                        RejectMessage rejectMessage = new RejectMessage(message);
                        rejectMessage.deserialize();
                        Client rejected = NetworkHandler.IDTable.get(rejectMessage.getID());
                        if(rejected != null){
                            NetworkHandler.chatNetwork.removeLink(rejected , client);
                            success = new ServerMessage("***YOUR INVITATION WAS REJECTD" +
                                    " BY "+client.id+"***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(rejected , success.getArray());
                            success = new ServerMessage("***REJECTION SUCCESSFUL***");
                            success.serialize();
                            NetworkHandler.toSendMessagesPut(client , success.getArray());
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
