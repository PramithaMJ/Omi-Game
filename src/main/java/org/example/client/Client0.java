package org.example.client;

import org.example.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client0 {
    private static Server server;
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    private String username;
//    private Server server;

    public Client0(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch(IOException e){
            closeEverything(socket,bufferedWriter,bufferedReader);
        }





    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+": " + messageToSend);

                System.out.println("Message sent: " + messageToSend);

                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }catch(IOException e){
            closeEverything(socket,bufferedWriter,bufferedReader);
        }
    }


    public void listenForMessage(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 String msgFromGroupChat;

                 while(socket.isConnected()){
                     try{
                         msgFromGroupChat = bufferedReader.readLine();
                         System.out.println(msgFromGroupChat);

                     }catch(IOException e){
                         closeEverything(socket,bufferedWriter,bufferedReader);
                     }
                 }
             }
         }).start();
    }





    public void closeEverything(Socket socket,BufferedWriter bufferedWriter,BufferedReader bufferedReader){
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        Thread serverThread = new Thread(server);
        serverThread.start();
//        server.startServer();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the omi game: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",1234);

        Client0 client1 = new Client0(socket,username);
        client1.listenForMessage();
        client1.sendMessage();

    }



}
