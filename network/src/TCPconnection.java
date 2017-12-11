

import java.io.*;
import java.net.Socket;

public class TCPconnection {

    private final Socket socket;
    private final Thread rxThread;
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

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPconnection.this);
                    while(!rxThread.isInterrupted())
                    {
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPconnection.this, msg);
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    eventListener.onException(TCPconnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPconnection.this);

                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String msg)
    {
        try {
            out.write(msg + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPconnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect()
    {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPconnection.this, e);
        }
    }

    @Override
    public String toString()
    {
        return "TCPConnection: " + socket.getInetAddress()+ ": " + socket.getPort();
    }

}
