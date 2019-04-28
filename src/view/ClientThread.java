package view;

import javafx.application.Platform;
import model.Card;
import model.Hand;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ClientThread extends Thread{

    private ClientController client;
    private LinkedList<Card> tempList;

    public ClientThread(ClientController client) {
        this.client = client;
        tempList = new LinkedList<>();
    }

    public void run() {

        /*DEBUG*/System.out.println(getId()+": Thread started");

        while(client.getState() == ClientController.ClientState.LOBBY) {

            try {
                List<String> mes = new ArrayList<>(Arrays.asList(client.readFromServer().split(":")));

                if(mes.size() == 0) continue;

                //*DEBUG*/System.out.println(getId()+": got from server: "+mes);
                //Client State 1@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                if(mes.get(0).equals(ServerMessage.PLAYER_LIST.toString())){
                    //update gui
                    Platform.runLater(() -> {
                        mes.remove(0);
                        mes.forEach(name -> client.addPlayer(name));
                        client.gui.updateLobbyScene();
                    });
                }
                else if(mes.get(0).equals(ServerMessage.PLAYER_CONNECT.toString())){
                    Platform.runLater(() -> {
                        client.addPlayer(mes.get(1));
                        client.gui.updateLobbyScene();
                    });
                }
                else if(mes.get(0).equals(ServerMessage.PLAYER_DISCONNECT.toString())){
                    Platform.runLater(() -> {
                        client.removePlayer(mes.get(1));
                        client.gui.updateLobbyScene();
                    });
                }
                /* if(mes.get(0).equals(ServerMessage.START_GAME.toString())){
                    Platform.runLater(() -> {
                        //Initialize this client's variables
                        client.gui.yourID = Integer.parseInt(mes.substring(5,6)); //Set the playerID
                        client.gui.yourCards = new Hand();
                        client.gui.deckCount = Integer.parseInt(mes.substring(7));	//Temporarily putting the amount of players here
                        client.gui.cardCounts = new int[client.gui.deckCount];
                        client.gui.playerPairs = new List[client.gui.deckCount];
                        for(int i = 0; i < client.gui.deckCount; ++i)
                            client.gui.playerPairs[i] = new ArrayList<Card>();
                        //Send this client to the client state
                        client.gui.game();
                    });
                }
                //Client State 2@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                else if(client.gui.state.equals("lobby") || client.gui.state.equals("hosting")) {
                    Platform.runLater(() -> System.out.println(mes));
                }
                //Client State 3@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                else if(client.gui.state.equals("client")){
                    //&&&&&Part 1&&&&&
                    //Check to see if we have a valid message that has semicolons
                    if(mes.indexOf(';') == -1) continue;


                    //&&&&Part 2&&&&&
                    Platform.runLater(() -> {
                        //Update players messages about the previous move and whose turn it is

                        //Tells the Client what move was made
                        System.out.println(mess[1]);

                        //Determines if this Client is the one to go next
                        if(mess[2].equals(client.gui.yourName)) {
                            System.out.println("It's your turn");
                            //client.gui.root.getChildren().add(client.gui.playButton);
                        }
                        else {
                            System.out.println("It's " + mess[2] + "'s turn");
                        }

                        System.out.println(client.gui.yourCards == null);

                        //Giving players their cards
                        client.fillHand(mess[2], mess[3]);

                        //Test if client got the message
                        System.out.println(mes);

                        //Update each Client how many cards are in the Draw Deck
                        //Update each client on how many card each player has
                        client.setCardCounts(mess[4], mess[5]);

                        //Update each client on which matches each player has
                        client.setPlayerPairs(mess[6]);
                    });
                }
                //Client State 4@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //Used with Server stage 5
                //Making changes to the clients
                //Sending them to the win screen and closing down all sockets
                else if(mes.length()>5 && mes.substring(0, 6).equals("Winner"))
                {

                }*/

            } catch (IOException e) {

            }
        }
        System.out.println("Stage 1 Ended");
        /*DEBUG*/System.out.println(getId()+": Thread ended");

    }
}
