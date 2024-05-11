package org.example.server;

import org.example.OmiGameLogic.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.OmiGameLogic.OmiGame;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clients = new ArrayList<>();
    }

    // responsible for running the server
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                // until a client is connecting this will wait for here
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected");

                // Create a new client handler
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);

                // Start a new thread to handle client communication
                Thread thread = new Thread(clientHandler);
                thread.start();

                // Check if we have four clients connected
                if (clients.size() == 4) {
                    startGame();
                    break; // Exit the loop after starting the game
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("Starting Omi Game...");
        OmiGame omiGame = new OmiGame();
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

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
