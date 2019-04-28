package view;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ServerController {

    private static final int SERVER_PORT = 0;
    ClientGUI gui;

    enum ServerState{
        UNINITIALIZED, LOBBY, GAME
    }

    //server stuff
    ServerSocket server;
    private ServerState state;
    private HashMap<String, Socket> clients;

    ServerController(ClientGUI gui){
        this.gui = gui;
        clients = new HashMap<>();
        state = ServerState.UNINITIALIZED;
    }

    boolean setupHost() {
        for(int attempts = 0; attempts < 10; attempts++){//tries ten times to create the server
            /*DEBUG*/System.out.println("Trying to make host...");
            try{
                server = new ServerSocket(SERVER_PORT);//throws IOException if port is taken
                state = ServerState.LOBBY;
                String hostIP = server.getInetAddress().getHostAddress() +":"+ server.getLocalPort();
                ServerThread serverThread = new ServerThread(this);
                serverThread.start();
                /*DEBUG*/System.out.println("Made host: " + hostIP);

                //put ip in clipboard to make my life easier
                StringSelection data = new StringSelection(hostIP);
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents(data, data);
                return true;
            }
            catch(IOException ignored){}
        }
        gui.showAlert("Failed to Establish Host");
        return false;
    }

    void startGame() {
        state = ServerState.GAME;
        try {
            writeToAllClients(ServerMessage.START_GAME.toUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeSocks() {
        try{
            server.close();
        }
        catch(Exception ignored){}
        server = null;
        for(Socket s : clients.values()) {
            try{
                s.close();
            }
            catch(Exception ignored){}
        }
        clients.clear();
    }

    ServerState getState() {
        return state;
    }

    boolean isHosting(){
        return server != null;
    }

    String getServerAddress(){
        if(server == null) return null;
        return server.getInetAddress().getHostAddress() + ":" + server.getLocalPort();
    }

    void addClient(String clientName, Socket clientSocket){
        clients.put(clientName, clientSocket);
    }

    void removeClient(String clientName){
        clients.remove(clientName);
    }

    List<String> getClients(){
        return new ArrayList<>(clients.keySet());
    }

    Socket getClientSocket(String clientName){
        return clients.get(clientName);
    }

    void writeToClient(String client, String mes) throws IOException {
        DataOutputStream out = new DataOutputStream(clients.get(client).getOutputStream());
        out.writeUTF(mes);
    }

    String readFromClient(String client) throws IOException {
        DataInputStream in = new DataInputStream(clients.get(client).getInputStream());
        return in.readUTF();
    }

    void writeToAllClients(String mes) throws IOException {
        for(String client : clients.keySet()){
            writeToClient(client, mes);
        }
    }

}
