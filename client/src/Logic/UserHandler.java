package Logic;

import Messages.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class UserHandler{
    protected boolean loggedIn = false;
    ServiceStopListener ssl;
    ConsoleWriter consoleWriter;
    ConsoleReader consoleReader;
    boolean go = false;
    public UserHandler(){
        consoleWriter = new ConsoleWriter();
        consoleReader = new ConsoleReader();
    }
    public void addServiceStopListener(ServiceStopListener ssl){
        this.ssl = ssl;
    }
    public void suspend(){
        go = false;
        loggedIn = false;
    }
    synchronized public void resume(){
        go = true;
        this.notifyAll();
    }
    public Message interpret(String input){
        Message output = null;
        if (input.matches("login [a-zA-Z_0-9]+")){
            if(!loggedIn) {
                String ID = input.substring(input.indexOf(" ")+1);
                output = new LoginMessage(ID);
                loggedIn = true;
            }
            else
                System.out.println("***YOU ARE ALREADY LOGGED-IN***");
        }
        else if (input.matches("logoff") || input.matches("logout")) {
            if(loggedIn) {
                output = new LogoffMessage();
                loggedIn = false;
            }
            else
                System.out.println("***YOU ARE NOT LOGGED-IN***");
        }
        else if (input.matches("help")){
            System.out.println("command: login [ID]\ndescription: logs you in with the ID specified\n\n" +
                    "command: logoff/logout\n\tdescription: logs you off\n\n" +
                    "command: @[ID] [message]\n\tdescription: sends the specified message to the specified ID\n\n" +
                    "command: invite [ID]\n\tdescription: invites the specified ID to chat if they're online\n\n" +
                    "command: accept [ID]\n\tdescription: accepts invitation from specified ID if they're online\n\n" +
                    "command: reject [ID]\n\tdescription: rejects invitation from specified ID if they're online\n\n" +
                    "command: invitations -opt\n\tdescription: opt:'i' provides you with a list of those you invited" +
                    " and opt:'o' provides you with a list of those invited you\n\n" +
                    "command: chat -opt [ID]\n\tdescription: opt:'l' provides you with a list of current chats and " +
                    "opt:'q' quits chat with specified ID\n\n" +
                    "command: stop\n\tdescription: disconnects from the server\n\n" +
                    "command: quit\n\tdescription: quits the program\n\n");
        }
        else if (input.matches("@[a-zA-Z_0-9]+ .+") && loggedIn) {
            String ID = input.substring(1,input.indexOf(" "));
            String message = input.substring(input.indexOf(" ")+1);
            output = new ChatMessage(ID , message);
        }
        else if(input.matches("invite [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" ")+1);
            output = new InviteMessage(ID);
        }
        else if(input.matches("accept [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" ")+1);
            output = new AcceptMessage(ID);
        }
        else if(input.matches("reject [a-zA-Z_0-9]+") && loggedIn){
            String ID = input.substring(input.indexOf(" ")+1);
            output = new RejectMessage(ID);
        }
        else if((input.matches("invite +-i +-l *") || input.matches("invite +-l +-i *")) && loggedIn){
            output = new InvitationListRequest(0);
        }
        else if((input.matches("invite +-o +-l *") || input.matches("invite +-l +-o *")&& loggedIn)){
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
            ssl.notConnectedStage();
        }
        else if(input.matches("quit")){
            System.out.println("***FIRST DISCONNECT FROM THE SERVER USING COMMAND \"STOP\"***");
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
                if(!go) {
                    System.out.println("***TYPE IN \"START\" TO TRY TO CONNECT TO SERVER AND \"QUIT\" TO QUIT" +
                            "(CASE INSENSITIVE)***\n");
                }
                input = sc.nextLine();
                if(!go){
                    ssl.tryConnection(input);
                }
                else {
                    Message toSendMessage = interpret(input);
                    try {
                        toSendMessage.serialize();
                        NetworkHandler.toSendMessages.add(toSendMessage.getArray());
                    } catch (NullPointerException e) {
                    }
                }
            }while(true);
        }
    }
    private class ConsoleWriter implements Runnable{
        Thread thread;
        public ConsoleWriter() {
            thread = new Thread(this);
            thread.start();
        }
        @Override
        public void run() {
            while(true){
                synchronized (UserHandler.this){
                    if(!go) {
                        try {
                            UserHandler.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(!NetworkHandler.receivedMessages.isEmpty()){
                    while(!NetworkHandler.receivedMessages.isEmpty()){
                        byte[] messageArr = NetworkHandler.receivedMessages.poll();
                        switch (messageArr[0]) {
                            case MessageTypes.CHAT_MESSAGE:
                                ChatMessage chatMessage = new ChatMessage(messageArr);
                                chatMessage.deserialize();
                                System.out.println(chatMessage.getID()+": "+chatMessage);
                                break;
                            case MessageTypes.SERVER_MESSAGE:
                                ServerMessage message = new ServerMessage(messageArr);
                                message.deserialize();
                                System.out.println(message);
                                break;
                            case MessageTypes.CHAT_LIST_RESULT:
                                ChatListResult result = new ChatListResult(messageArr);
                                result.deserialize();
                                LinkedHashSet<String> list = result.getChatList();
                                Iterator<String> iterator = list.iterator();
                                if (list.size() == 0) {
                                    System.out.println("***YOU ARE NOT CHATTING WITH ANYBODY***");
                                } else {
                                    System.out.println("***YOU ARE CURRENTLY CHATTING WITH:***");
                                    int c=0;
                                    while (iterator.hasNext()) {
                                        System.out.println((++c)+"- "+iterator.next());
                                    }
                                }
                                break;
                            case MessageTypes.INVITATION_LIST_RESULT:
                                InvitationListResult invitationResult = new InvitationListResult(messageArr);
                                invitationResult.deserialize();
                                LinkedHashSet<String> invitationList = invitationResult.getList();
                                iterator = invitationList.iterator();
                                if(invitationList.size() == 0)
                                    System.out.println("***THE LIST YOU REQUESTED IS EMPTY***");
                                else {
                                    System.out.println("***THE LIST YOU REQUESTED:***");
                                    int c=0;
                                    while (iterator.hasNext()) {
                                        System.out.println((++c)+"- "+iterator.next());
                                    }
                                }
                                break;
                            case MessageTypes.LOGIN: //this indicates login failure
                                LoginMessage loginMessage = new LoginMessage(messageArr);
                                loginMessage.deserialize();
                                System.out.println("***"+loginMessage.getID()+" IS ALREADY LOGGED-IN " +
                                        "THE SERVER***");
                                loggedIn = false;
                                break;
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
