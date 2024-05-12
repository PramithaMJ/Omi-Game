package org.example.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client4 {
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    private String username;

    public Client4(Socket socket,String username){
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
                bufferedWriter.write(messageToSend);
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

    public void sendMessageFromServer(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the omi game: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",1235);
        Client4 client4 = new Client4(socket,username);
        client4.listenForMessage();
        client4.sendMessage();
    }
}