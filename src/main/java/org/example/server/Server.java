package org.example.server;

import org.example.OmiGameLogic.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.OmiGameLogic.OmiGame;
import org.example.client.Client2;

import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;
    private ClientHandler clientHandlertolisten;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clients = new ArrayList<>();
    }

    // responsible for running the server
    public void startServer() throws IOException {

        try {
            while (!serverSocket.isClosed()) {
                // until a client is connecting this will wait for here
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected");

                // Create a new client handler
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);

//                clientHandler.broadcastMessage("hello");
                // Start a new thread to handle client communication


                Thread thread = new Thread(clientHandler);
                thread.start();

                // Check if we have four clients connected
                if (clients.size() == 5) {
                    startGame();
                    break; // Exit the loop after starting the game
                }

                //when message is received from the client

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("Starting Omi Game...");
        OmiGame omiGame = new OmiGame(
                this.clients
        );
        omiGame.playGame();
    }



    public void closeServerSocket() { // handle the ioexception occur in the start server method
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void init() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }}
}
