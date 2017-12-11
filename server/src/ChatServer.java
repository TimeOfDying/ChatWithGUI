import java.io.IOException;
import java.net.ServerSocket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ChatServer implements TCPconnectionListener{

    private static Logger log = Logger.getLogger(ChatServer.class.getName());
    private final ArrayList<TCPconnection> connectionsList = new ArrayList<>();

    private ChatServer()
    {
        try {
            ServerSocket serverSocket = new ServerSocket(2222);
            log.log(Level.INFO, "Сервер запущен");
            while (true)
            {
                try
                {
                    new TCPconnection(this, serverSocket.accept());

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
    public synchronized void onConnectionReady(TCPconnection tcpconnection) {
        connectionsList.add(tcpconnection);
        log.log(Level.INFO, "Пользователь подключился");
        sendToAllUsers("Пользователь подключился: " + tcpconnection);
    }

    @Override
    public synchronized void onReceiveString(TCPconnection tcpconnection, String value) {
        sendToAllUsers(value);
    }

    @Override
    public synchronized void onDisconnect(TCPconnection tcpconnection) {
        connectionsList.remove(tcpconnection);
        log.log(Level.INFO, "Пользователь отключился ");
        sendToAllUsers("Пользователь отключился: " + tcpconnection);

    }

    @Override
    public synchronized void onException(TCPconnection tcPconnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllUsers(String msg)
    {
        System.out.println(msg);
        for(int i=0;i<connectionsList.size(); i++)
        {
            connectionsList.get(i).sendString(msg);
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

