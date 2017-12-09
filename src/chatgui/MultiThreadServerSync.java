package chatgui;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


public class MultiThreadServerSync {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {


        int portNumber = 2222;


        try
        {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Сервер запущен. " + "Используется порт = " + portNumber);
        }
        catch (IOException e)
        {
            System.out.println(e);
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
                        break;
                    }
                }
                if (i == maxClientsCount)
                {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Сервер перегружен");
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