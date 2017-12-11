import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ChatClient extends JFrame implements ActionListener , ConnectionEvents {

    private static final String ip = "localhost";
    private static final int port = 2222;

    private final JTextArea messagesField = new JTextArea();
    private final JTextField fNickname = new JTextField();
    private final JTextField fMessage = new JTextField();
    private Connection connection;

    private ChatClient()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
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
            connection = new Connection(this , ip, port);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fMessage.getText();
        if(msg.equals("")) return;
        fMessage.setText(null);
        connection.SendString(fNickname.getText() + ": " + msg);
    }

    @Override
    public void ConnectionReady(Connection connection) {
        printMessage("Соединение установлено");
    }

    @Override
    public void ReceiveString(Connection connection, String value) {
        printMessage(value);
    }

    @Override
    public void Disconnect(Connection connection) {
        printMessage("Соединение закрыто");
    }

    @Override
    public void Exception(Connection connection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String msg)
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
