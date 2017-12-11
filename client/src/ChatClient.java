import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ChatClient extends JFrame implements ActionListener , TCPconnectionListener{

    private static final String ip = "localhost";
    private static final int port = 2222;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final JTextArea messagesField = new JTextArea();
    private final JTextField fNickname = new JTextField();
    private final JTextField fMessage = new JTextField();
    private TCPconnection connection;

    private ChatClient()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        messagesField.setEditable(false);
        messagesField.setLineWrap(true);
        fMessage.addActionListener(this);
        add(messagesField, BorderLayout.CENTER);
        add(fNickname, BorderLayout.NORTH);
        add(fMessage, BorderLayout.SOUTH);

        try {
            connection = new TCPconnection(this , ip, port);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fMessage.getText();
        if(msg.equals("")) return;
        fMessage.setText(null);
        connection.sendString(fNickname.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPconnection tcpconnection) {
        printMsg("Connection ready");
    }

    @Override
    public void onReceiveString(TCPconnection tcpconnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPconnection tcpconnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPconnection tcPconnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messagesField.append(msg + "\n");
                messagesField.setCaretPosition(messagesField.getDocument().getLength());
            }
        });
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClient();
            }
        });
    }
}
