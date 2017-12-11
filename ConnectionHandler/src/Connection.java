

import java.io.*;
import java.net.Socket;

public class Connection {

    private final Socket socket;
    private final Thread thread;
    private ConnectionEvents eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;


    public Connection(ConnectionEvents eventListener, Socket socket) throws IOException
    {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.ConnectionReady(Connection.this);
                    while(!thread.isInterrupted())
                    {
                        String msg = in.readLine();
                        eventListener.ReceiveString(Connection.this, msg);
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    eventListener.Exception(Connection.this, e);
                } finally {
                    eventListener.Disconnect(Connection.this);

                }
            }
        });
        thread.start();
    }

    public Connection(ConnectionEvents eventListener, String ip, int port) throws IOException
    {
        this(eventListener, new Socket(ip,port));
    }

    public synchronized void SendString(String msg)
    {
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.Exception(Connection.this, e);
            Disconnect();
        }
    }

    public synchronized void Disconnect()
    {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.Exception(Connection.this, e);
        }
    }

    @Override
    public String toString()
    {
        return socket.getInetAddress()+ ": " + socket.getPort();
    }

}
