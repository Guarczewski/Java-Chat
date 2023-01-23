package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

class ServerThread {

    private boolean clientIdSent = false;

    final int clientID;
    final String clientName;
    final Socket socket;
    final ObjectInputStream objectInputStream;
    final ObjectOutputStream objectOutputStream;

    public List<String> msgToSend;

    ServerThread(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, int clientID) {
        this.socket = socket;
        this.clientID = clientID;
        this.clientName = "Client_0" + clientID;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;

        msgToSend = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> strangerPackage = new ArrayList<>();
                List<String> acquiredList;

                while (true) {
                    try { // Wysyłanie
                        if (!clientIdSent) {
                            objectOutputStream.writeObject(clientID);
                            clientIdSent = true;
                        }
                        else {
                            objectOutputStream.reset();
                            objectOutputStream.flush();
                            strangerPackage.addAll(msgToSend);
                            msgToSend.clear();
                            objectOutputStream.writeUnshared(strangerPackage);
                            strangerPackage.clear();
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    try { // Odbieranie
                        acquiredList = (List<String>) objectInputStream.readObject();
                        for (ServerThread serverThread : Server.threadList)
                            if (serverThread.clientID != clientID)
                                serverThread.msgToSend.addAll(acquiredList);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }
            }
        }).start();

    }

}
