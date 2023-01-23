package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

public class Server {

    private static final int port = 7777;

    public static List<ServerThread> threadList;

    private  int clientCount = 0;

    Server() {
        threadList = new ArrayList<>();

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ServerSocket finalServerSocket = serverSocket;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Socket socket = null;
                    try {

                        System.out.println("Serwer czeka na klienta...");
                        socket = finalServerSocket.accept();
                        System.out.println("Serwer widzi nowego klienta");
                        clientCount++;

                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        threadList.add(new ServerThread(socket, ois, oos, clientCount));


                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public static void main(String[] args) {
        new Server();
    }



}
