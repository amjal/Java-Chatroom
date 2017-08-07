
import java.util.Scanner;

public class UserHandler{
    public UserHandler(){
        new ConsoleReader();
        new ConsoleWriter();
    }
    public Message interpret(String input){
        Message output;
        output = new LoginMessage("amjal");
        output.serialize();
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
               NetworkHandler.toSendMessages.add((interpret(input).array));
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
                        Message message;
                        switch (messageArr[0]){
                            case MessageTypes.SERVER_MESSAGE:
                                message = new ServerMessage(messageArr);
                                message.deserialize();
                                System.out.println(message);
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
