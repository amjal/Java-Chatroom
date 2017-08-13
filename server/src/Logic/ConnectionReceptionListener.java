package Logic;

import java.nio.channels.SocketChannel;

public interface ConnectionReceptionListener{
    void onNewConnectionReceived(SocketChannel connection);
}
