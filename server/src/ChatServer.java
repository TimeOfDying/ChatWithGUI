import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ChatServer implements ConnectionEvents {

    private static Logger log = Logger.getLogger(ChatServer.class.getName());
    private final ArrayList<Connection> connectionsList = new ArrayList<>();

    private ChatServer()
    {
        try {
            ServerSocket serverSocket = new ServerSocket(2222);
            log.log(Level.INFO, "Сервер запущен");
            while (true)
            {
                try
                {
                    new Connection(this, serverSocket.accept());

                } catch (IOException e)
                {
                    log.log(Level.SEVERE, "Не удалось запустить сервер", e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void ConnectionReady(Connection connection) {
        connectionsList.add(connection);
        log.log(Level.INFO, "Пользователь подключился");
        sendToAllUsers("Пользователь подключился: " + connection);
    }

    @Override
    public synchronized void ReceiveString(Connection connection, String value) {
        sendToAllUsers(value);
    }

    @Override
    public synchronized void Disconnect(Connection connection) {
        connectionsList.remove(connection);
        log.log(Level.INFO, "Пользователь отключился ");
        sendToAllUsers("Пользователь отключился: " + connection);

    }

    @Override
    public synchronized void Exception(Connection connection, Exception e) {
        log.log(Level.SEVERE, "Exception: ", e);
    }

    private void sendToAllUsers(String msg)
    {
        System.out.println(msg);
        for(int i=0;i<connectionsList.size(); i++)
        {
            connectionsList.get(i).SendString(msg);
        }
    }

    private static void InitLoader()
    {
        try {
            LogManager.getLogManager().readConfiguration(
                    ChatServer.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Не удалось создать лог. ", e);
        }
    }
    public static void main(String[] args)
    {
        InitLoader();
        new ChatServer();
    }

}

