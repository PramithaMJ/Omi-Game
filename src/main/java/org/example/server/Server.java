package org.example.server;

import org.example.OmiGameLogic.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    protected ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    //responsible for running the server
    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                //until a client is connecting this will wait for here
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected");
                ClientHandler clientHandler = new ClientHandler(socket); //objects of this class are responsible for keeping communication with clients

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){

        }
    }

    public void closeServerSocket(){//handle the ioexception occur in the start server method
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
                e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
