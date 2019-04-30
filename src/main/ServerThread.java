package main;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

class ServerThread extends Thread{

    private ServerController server;

    ServerThread(ServerController server) {
        this.server = server;
    }

    public void run() {

        try {
            server.server.setSoTimeout(10000);
        }
        catch(SocketException e){
            System.out.println("Unable to establish host");
        }
        //1st Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        while(server.getState() == ServerController.ServerState.LOBBY) {
            try {
                Socket clientSocket = server.server.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                String clientName = in.readUTF();
                server.writeToAllClients(ServerMessage.PLAYER_CONNECT.toUTF() + clientName);
                server.addClient(clientName, clientSocket, in);
                server.writeToClient(clientName, ServerMessage.PLAYER_LIST.toUTF() + String.join(":", server.getClients()));
            }
            catch(SocketTimeoutException e){
                System.out.println("No clients connected.");
            }
            catch(IOException e){
                System.out.println("IOException during accept()");
            }
            catch(NullPointerException e){
                System.out.println("NullPointerException during something");
            }

            for(String client: server.getClients()){
                if(server.getClientSocket(client).isClosed()){
                    server.removeClient(client);
                }
            }
        }
        server.initializeGame();
        System.out.println("Server Thread finished");
    }
}
