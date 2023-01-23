package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.List;


public class Client extends JFrame implements ActionListener {

    public static Client Main;

    public int ClientID = -1;
    public String ClientName;

    public JTextArea msgBox;
    public JTextArea inBox;

    public JButton sendButt;
    public JButton closeButt;

    static public List<String> msgToSend;

    Client(){
        // Basic JFrame Thingy
        super("Client");
        final int WINDOW_WIDTH = 1280;
        final int WINDOW_HEIGHT = 720;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100,100,WINDOW_WIDTH,WINDOW_HEIGHT);

        JPanel mainJPanel = new JPanel(new BorderLayout());
        JPanel userJPanel = new JPanel(new BorderLayout());
        msgBox = new JTextArea();
        inBox = new JTextArea();
        sendButt = new JButton("Wyślij Wiadomość");
        closeButt = new JButton("Wyjdź z Czatu");

        sendButt.addActionListener(this);
        closeButt.addActionListener(this);

        mainJPanel.add(msgBox, BorderLayout.CENTER);
        mainJPanel.add(userJPanel, BorderLayout.SOUTH);

        userJPanel.add(closeButt, BorderLayout.WEST);
        userJPanel.add(inBox, BorderLayout.CENTER);
        userJPanel.add(sendButt, BorderLayout.EAST);

        setContentPane(mainJPanel);
        setResizable(false);
        setVisible(true);

        msgToSend = new ArrayList<>();

        Socket socket = null;
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            socket = new Socket("localhost",  7777);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (Exception e){
            System.out.println("Error during creating connection");
        }

        ObjectInputStream finalObjectInputStream = objectInputStream;
        ObjectOutputStream finalObjectOutputStream = objectOutputStream;

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> AcquiredList;
                List<String> tempList = new ArrayList<>();
                StringBuilder tempBoxContainer;

                while (true) {
                    try {
                        if (ClientID == -1) {
                            System.out.println("Joined Chat");
                            int tmp = (int) finalObjectInputStream.readObject();
                            ClientName = "Client_0" + tmp;
                            ClientID = tmp;
                            setTitle(ClientName);
                        }
                        else {
                           try {
                               AcquiredList = (List<String>) finalObjectInputStream.readObject();
                               tempBoxContainer = new StringBuilder(msgBox.getText());

                               for (String tempString : AcquiredList)
                                   tempBoxContainer.append(tempString).append("\n");


                               msgBox.setText(tempBoxContainer.toString());

                               AcquiredList.clear();
                           }
                           catch (Exception exception) {
                               exception.printStackTrace();
                           }

                        }


                    } catch (Exception e) {
                       e.printStackTrace();
                    }

                    try {

                        tempList.addAll(msgToSend);
                        msgToSend.clear();

                        finalObjectOutputStream.reset();
                        finalObjectOutputStream.flush();
                        finalObjectOutputStream.writeUnshared(tempList);
                        tempList.clear();

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
            }
        }).start();

    }


    public static void main(String[] args) {
        Main = new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButt) {
            String tempString = Main.ClientName + ": " + inBox.getText();
            msgToSend.add(tempString);
            msgBox.setText(Main.msgBox.getText() + tempString + "\n");
        }
    }
}
