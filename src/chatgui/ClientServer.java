package chatgui;

import javax.swing.*;

public class ClientServer {


    public static void main(String [] args){

        Object[] selectionValues = {"Server","Client"};
        String initialSection = "Server";

        Object selection = JOptionPane.showInputDialog(null, "Login as : ", "ChatApplication", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSection);
        if(selection.equals("Server"))
        {
            String[] arguments = new String[] {};
            new MultiThreadServerSync().main(arguments);
        }
        else if(selection.equals("Client"))
        {
            String IPServer = JOptionPane.showInputDialog("Введите ip сервера");
            String[] arguments = new String[] {IPServer};
            new Client().main(arguments);
        }

    }

}