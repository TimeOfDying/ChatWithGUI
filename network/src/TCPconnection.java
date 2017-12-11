

import java.io.*;
import java.net.Socket;

public class TCPconnection {

    private final Socket socket;
    private final Thread thread;
    private TCPconnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPconnection(TCPconnectionListener eventListener, String ip, int port) throws IOException
    {
        this(eventListener, new Socket(ip,port));
    }

    public TCPconnection(TCPconnectionListener eventListener, Socket socket) throws IOException
    {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.ConnectionReady(TCPconnection.this);
                    while(!thread.isInterrupted())
                    {
                        String msg = in.readLine();
                        eventListener.ReceiveString(TCPconnection.this, msg);
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    eventListener.Exception(TCPconnection.this, e);
                } finally {
                    eventListener.Disconnect(TCPconnection.this);

                }
            }
        });
        thread.start();
    }

    public synchronized void sendString(String msg)
    {
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.Exception(TCPconnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect()
    {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.Exception(TCPconnection.this, e);
        }
    }

    @Override
    public String toString()
    {
        return "TCPConnection: " + socket.getInetAddress()+ ": " + socket.getPort();
    }

}
