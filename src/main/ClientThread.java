package main;

import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ClientThread extends Thread{

    private ClientController client;

    ClientThread(ClientController client) {
        this.client = client;
    }

    public void run() {

        /*DEBUG*/System.out.println(getId()+": Client Thread started");

        while(client.getState() == ClientController.ClientState.LOBBY) {

            try {
                List<String> mes = new ArrayList<>(Arrays.asList(client.readFromServer().split(":")));

                if(mes.size() == 0) continue;

                //*DEBUG*/System.out.println(getId()+": got from server: "+mes);
                //Client State 1@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                if(mes.get(0).equals(ServerMessage.PLAYER_LIST.toString())){
                    mes.remove(0);
                    mes.forEach(name -> client.addPlayer(name));
                    Platform.runLater(() -> client.gui.updateLobbyScene());
                }
                else if(mes.get(0).equals(ServerMessage.PLAYER_CONNECT.toString())){
                    client.addPlayer(mes.get(1));
                    Platform.runLater(() -> client.gui.updateLobbyScene());
                }
                else if(mes.get(0).equals(ServerMessage.PLAYER_DISCONNECT.toString())){
                    client.removePlayer(mes.get(1));
                    Platform.runLater(() -> client.gui.updateLobbyScene());
                }
                else if(mes.get(0).equals(ServerMessage.START_GAME.toString())){
                    client.startGame();
                    break;
                }
            } catch (IOException ignored) {}
        }
        /*DEBUG*/System.out.println(getId()+": Client Thread ended");

    }
}
