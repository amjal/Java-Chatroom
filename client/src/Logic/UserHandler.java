package Logic;

import Messages.*;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class UserHandler{
    protected boolean loggedIn = false;
    public UserHandler(){
        offlineStageHandler();
    }
    public void offlineStageHandler(){
        //TODO write the offline stage code
        new ConsoleReader();
        new ConsoleWriter();
    }
    public Message interpret(String input){
        Message output = null;
        if (input.matches("login [a-zA-Z_0-9]+")){
            if(!loggedIn) {
                String ID = input.substring(input.indexOf(" "));
                output = new LoginMessage(ID);
                loggedIn = true;
            }
            else
                System.out.println("***YOU ARE ALREADY LOGGED-IN***");
        }
        else if (input.matches("logoff")) {
            if(loggedIn) {
                output = new LogoffMessage();
                loggedIn = false;
            }
            else
                System.out.println("***YOU ARE NOT LOGGED-IN***");
        }
        else if (input.matches("help")){
            System.out.println("command: login [ID]\ndescription: logs you in with the ID specified\n\n" +
                    "command: logoff\n\tdescription: logs you off\n\n" +
                    "command: @[ID] [message]\n\tdescription: sends the specified message to the specified ID\n\n" +
                    "command: invite [ID]\n\tdescription: invites the specified ID to chat if they're online\n\n" +
                    "command: accept [ID]\n\tdescription: accepts invitation from specified ID if they're online\n\n" +
                    "command: reject [ID]\n\tdescription: rejects invitation from specified ID if they're online\n\n" +
                    "command: invitations -opt\n\tdescription: opt:'i' provides you with a list of those you invited" +
                    " and opt:'o' provides you with a list of those invited you\n\n" +
                    "command: chat -opt [ID]\n\tdescription: opt:'l' provides you with a list of current chats and " +
                    "opt:'q' quits chat with specified ID\n\n");
        }
        else if (input.matches("@[a-zA-Z_0-9]+ .+") && loggedIn) {
            String ID = input.substring(1,input.indexOf(" "));
            String message = input.substring(input.indexOf(" ")+1);
            output = new ChatMessage(ID , message);
        }
        else if(input.matches("invite [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" "));
            output = new InviteMessage(ID);
        }
        else if(input.matches("accept [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" "));
            output = new AcceptMessage(ID);
        }
        else if(input.matches("reject [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" "));
            output = new RejectMessage(ID);
        }
        else if(input.matches("invitations -i") && loggedIn){
            output = new InvitationListRequest(0);
        }
        else if(input.matches("invitations -o") && loggedIn){
            output = new InvitationListRequest(1);
        }
        else if(input.matches("chat -l") && loggedIn){
            output = new ChatListRequest();
        }
        else if(input.matches("chat -q [a-zA-Z_0-9]+")&& loggedIn){
            String ID = input.substring(8);
            output = new QuitChatRequest(ID);
        }
        else if(input.matches("stop")){
            output = new LogoffMessage();
            //TODO stop all threads via listener
            //TODO find a way to get into offlineStageHandler safely
        }
        else if(!loggedIn){
            System.out.println("***YOU ARE NOT LOGGED-IN***");
        }
        else{
            System.out.println("***INVALID COMMAND***");
        }
        return output;
    }
    private class ConsoleReader implements Runnable{
        Thread thread;
        Scanner sc;
        public ConsoleReader(){
            sc = new Scanner(System.in);
            thread = new Thread(this);
            thread.start();
        }
        @Override
        public void run() {
            String input;
            do{
               input = sc.nextLine();
               Message toSendMessage = interpret(input);
               try {
                   toSendMessage.serialize();
                   NetworkHandler.toSendMessages.add(toSendMessage.getArray());
               }catch(NullPointerException e){

               }
            }while(true);
        }
    }
    private class ConsoleWriter implements Runnable{
        Thread thread;
        public ConsoleWriter(){
            thread = new Thread(this);
            thread.start();
        }
        @Override
        public void run() {
            while(true){
                if(!NetworkHandler.receivedMessages.isEmpty()){
                    while(!NetworkHandler.receivedMessages.isEmpty()){
                        byte[] messageArr = NetworkHandler.receivedMessages.poll();
                        switch (messageArr[0]) {
                            case MessageTypes.SERVER_MESSAGE:
                                ServerMessage message = new ServerMessage(messageArr);
                                message.deserialize();
                                System.out.println(message);
                                break;
                            case MessageTypes.CHAT_LIST_RESULT:
                                ChatListResult result = new ChatListResult(messageArr);
                                result.deserialize();
                                Set<Client> list = result.getChatList();
                                Iterator<Client> iterator = list.iterator();
                                if (list.size() == 0) {
                                    System.out.println("***YOU ARE NOT CHATTING WITH ANYBODY***");
                                } else {
                                    System.out.println("***YOU ARE CURRENTLY CHATTING WITH:***");
                                    while (iterator.hasNext()) {
                                        Client c = iterator.next();
                                        System.out.println(c.id);
                                    }
                                }
                                break;
                            case MessageTypes.INVITATION_LIST_RESULT:
                                InvitationListResult invitationResult = new InvitationListResult(messageArr);
                                invitationResult.deserialize();
                                Set<Client> invitationList = invitationResult.getList();
                                iterator = invitationList.iterator();
                                if(invitationList.size() == 0)
                                    System.out.println("***THE LIST YOU REQUESTED IS EMPTY***");
                                else {
                                    System.out.println("***THE LIST YOU REQUESTED:***");
                                    while (iterator.hasNext()) {
                                        Client client = iterator.next();
                                        System.out.println(client.id);
                                    }
                                }
                                break;
                        }
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
