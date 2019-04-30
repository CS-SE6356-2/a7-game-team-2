package main;

import model.Card;
import model.GoFishGame;
import model.Player;
import view.ClientGUI;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerController {

    private static final int SERVER_PORT = 0;
    private ClientGUI gui;

    enum ServerState{
        UNINITIALIZED, LOBBY, GAME
    }

    //server stuff
    ServerSocket server;
    private ServerState state;
    private HashMap<String, Socket> clients;
    private HashMap<String, DataInputStream> clientInputStreams;
    private HashMap<String, DataOutputStream> clientOutputStreams;
    private GoFishGame game;

    ServerController(ClientGUI gui){
        this.gui = gui;
        clients = new HashMap<>();
        clientInputStreams = new HashMap<>();
        clientOutputStreams = new HashMap<>();
        state = ServerState.UNINITIALIZED;
    }

    public boolean setupHost() {
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

    public void startGame() {
        state = ServerState.GAME;
    }
    void initializeGame() {
        try {
            writeToAllClients(ServerMessage.START_GAME.toUTF());
            game = new GoFishGame(new ArrayList<>(clients.keySet()), new ArrayList<>(clients.values()), new File("resources\\cardlist.txt"));
            game.shuffleCards();
            game.dealCards();
            for (Player player : game.getPlayerList()) {
                System.out.println(player.getCardListForUTF());
                writeToClient(player.getName(), ServerMessage.DEAL_HAND.toUTF() + player.getCardListForUTF());
            }
            game.sortPlayersInPlayOrder();
            gameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameLoop(){
        try{
            while(game.determineWinner() == null){
                Player currentTurn = game.getNextPlayer();
                writeToClient(currentTurn.getName(), ServerMessage.START_TURN.toUTF());
                String[] query = readFromClient(currentTurn.getName()).split(":");
                if(!query[0].equals(ServerMessage.QUERY.toString())) throw new IllegalStateException();
                while(game.queryPlayer(Card.Value.parseValue(query[1]), game.getPlayer(query[2]), game.getPlayer(query[3]))){
                    writeToClient(query[3], ServerMessage.QUERY.toUTF() + query[1]);
                    writeToClient(query[2], ServerMessage.QUERY_RESPONSE.toUTF() + currentTurn.getCardListForUTF());
                    writeToClient(currentTurn.getName(), ServerMessage.START_TURN.toUTF());
                    query = readFromClient(currentTurn.getName()).split(":");
                }
                writeToClient(query[2], ServerMessage.QUERY_RESPONSE.toUTF() + currentTurn.getCardListForUTF());
            }
            System.out.println("Winner is " + game.determineWinner().getName());
            writeToAllClients(ServerMessage.ANNOUNCE_WINNER.toUTF() + game.determineWinner().getName());
            closeSocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocks() {
        try{
            server.close();
        }
        catch(Exception ignored){}
        server = null;
        clients.values().forEach(socket -> {
            try{
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clients.clear();
    }

    ServerState getState() {
        return state;
    }

    public String getServerAddress(){
        if(server == null) return null;
        return server.getInetAddress().getHostAddress() + ":" + server.getLocalPort();
    }

    void addClient(String clientName, Socket clientSocket, DataInputStream inputStream) throws IOException {
        clients.put(clientName, clientSocket);
        clientInputStreams.put(clientName, inputStream);
        clientOutputStreams.put(clientName, new DataOutputStream(clientSocket.getOutputStream()));
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
        System.out.println("Sending Message from Server: " + mes);
        clientOutputStreams.get(client).writeUTF(mes);
    }

    private String readFromClient(String client) throws IOException {
        String ret = clientInputStreams.get(client).readUTF();
        System.out.println("Server Received Message: " + ret);
        return ret;
    }

    void writeToAllClients(String mes) throws IOException {
        for(String client : clients.keySet()){
            writeToClient(client, mes);
        }
    }

}
