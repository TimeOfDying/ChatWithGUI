package chatgui;

import java.io.PrintStream;
import java.io.IOException;
import java.util.logging.*;
import java.net.Socket;
import java.net.ServerSocket;


public class MultiThreadServerSync {

    private static Logger logger = Logger.getLogger(MultiThreadServerSync.class.getName());
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        try
        {
            FileHandler file = new FileHandler("MultiThreadServerSync.txt");
            logger.addHandler(file);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Не удалось создать лог.", e);
        }

        int portNumber = 2222;

        try
        {
            serverSocket = new ServerSocket(portNumber);
            logger.log(Level.INFO, "Сервер запущен. Используется порт = " + portNumber);
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Не удалось запустить сервер", e);
        }


        while (true)
        {
            try
            {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++)
                {
                    if (threads[i] == null)
                    {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        logger.log(Level.INFO, "Установлено соединение с " + clientSocket.getRemoteSocketAddress());
                        break;
                    }
                }
                if (i == maxClientsCount)
                {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Сервер перегружен");
                    logger.log(Level.INFO, "Пользователь " + clientSocket.getRemoteSocketAddress() + " не подключился из-за перегрузки");
                    os.close();
                    clientSocket.close();
                }
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }
    }
}