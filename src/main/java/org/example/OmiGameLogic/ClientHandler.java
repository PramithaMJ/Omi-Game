package org.example.OmiGameLogic;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //to keep track on all connected clients

    protected Socket socket;
    protected BufferedReader bufferedReader;
    protected BufferedWriter bufferedWriter;
    public String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            broadcastMessage("SERVER: " + clientUsername + " has entered the game!");
            clientHandlers.add(this);
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public static ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }


    //everything below run method will run on multiple separate threads
    @Override
    public void run() {
        String messageFromClient;

        try {
            while ((messageFromClient = bufferedReader.readLine()) != null) {
                broadcastMessage(clientUsername + ": " + messageFromClient);
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } finally {
            removeClientHandler();
        }
    }

    public void broadcastMessage2(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessageToSingleClient(String messageToSend,Player player) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(player.getName())) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the Game!");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClientHandler getClientHandlerForPlayer(Player player) {
        for (ClientHandler clientHandler : ClientHandler.getClientHandlers()) {
            if (clientHandler.clientUsername.equals(player.getName())) {
                return clientHandler;
            }
        }
        return null;
    }

    private String readInputFromClient(Player player) {
        ClientHandler clientHandler = getClientHandlerForPlayer(player);
        String input = null;
        try {
            input = clientHandler.bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public void sendMessageToPlayer(Player player, String message) {
        ClientHandler clientHandler = getClientHandlerForPlayer(player);
        if (clientHandler != null) {
            clientHandler.broadcastMessageToSingleClient(message,player);
        }
    }
}
