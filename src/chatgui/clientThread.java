package chatgui;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class clientThread extends Thread
{

    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads)
    {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run()
    {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try
        {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true)
            {
                os.println("Введите имя.");
                name = is.readLine().trim();
                if (name.indexOf('@') == -1)
                {
                    break;
                }
                else
                {
                    os.println("Имя не должно содержать символ '@'");
                }
            }

            os.println("Приветствуем " + name + ".\nЧтобы выйти, введите /quit в новой строке.");

            synchronized (this)
            {
                for (int i = 0; i < maxClientsCount; i++)
                {
                    if (threads[i] != null && threads[i] == this)
                    {
                        clientName = "@" + name;
                        break;
                    }
                }

                for (int i = 0; i < maxClientsCount; i++)
                {
                    if (threads[i] != null && threads[i] != this)
                    {
                        threads[i].os.println("*** Пользователь с ником " + name + " зашел в чат !!! ***");
                    }
                }
            }

            while (true)
            {
                String line = is.readLine();

                if (line.startsWith("/quit"))
                {
                    break;
                }

                if (line.startsWith("@"))
                {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null)
                    {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty())
                        {
                            synchronized (this)
                            {
                                for (int i = 0; i < maxClientsCount; i++)
                                {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0]))
                                    {
                                        threads[i].os.println("<" + name + "> " + words[1]);
                                        this.os.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    synchronized (this)
                    {
                        for (int i = 0; i < maxClientsCount; i++)
                        {
                            if (threads[i] != null && threads[i].clientName != null)
                            {
                                threads[i].os.println("<" + name + "> " + line);
                            }
                        }
                    }
                }
            }

            synchronized (this)
            {
                for (int i = 0; i < maxClientsCount; i++)
                {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null)
                    {
                        threads[i].os.println("*** Пользователь с ником " + name + " покидает чат !!! ***");
                    }
                }
            }

            os.println("*** До встречи, " + name + " ***");

            synchronized (this)
            {
                for (int i = 0; i < maxClientsCount; i++)
                {
                    if (threads[i] == this)
                    {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
        }
        catch (IOException e) { }
    }
}