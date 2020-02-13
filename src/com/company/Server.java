package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    ArrayList clientOutputStream; // Лист из потоков клиентов

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket){
            try {
                socket = clientSocket;
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
             String message;
             try{
             while ((message = reader.readLine()) != null ) {
                 System.out.println("Read " + message);
                 tellEveryone(message);
             }
             } catch (IOException e) {
                 e.printStackTrace();
             }
        }
    }

    public void go(){
        clientOutputStream = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            System.out.println("Сервер начал работу");

            while (true){
                Socket clientSocket = serverSocket.accept();

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer);

                Runnable runnable = new ClientHandler(clientSocket);
                Thread thread = new Thread(runnable);
                thread.start();
                System.out.println("Got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.go();
    }

    public void tellEveryone(String message){
        Iterator it = clientOutputStream.iterator();
        while (it.hasNext()){
            try {
                PrintWriter printWriter = (PrintWriter) it.next();
                printWriter.println(message);
                printWriter.flush();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
