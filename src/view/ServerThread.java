package view;

import javafx.application.Platform;
import model.GoFishGame;
import model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.stream.Collectors;

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
            Platform.runLater(() -> System.out.println("Unable to establish host"));
        }
        //1st Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        while(server.getState() == ServerController.ServerState.LOBBY) {
            try {
                Socket clientSocket = server.server.accept();
                /*DEBUG*/System.out.println("Client Connected");
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                String clientName = in.readUTF();
                server.writeToAllClients(ServerMessage.PLAYER_CONNECT.toUTF() + clientName);
                server.addClient(clientName, clientSocket);
                /*DEBUG*/System.out.println(clientName);
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
        System.out.println("1st Stage Finished");
        //2nd Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //3rd Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Temporarily reset the state to hosting so the server's client can receive the message properly
        server.gui.state = "hosting";

        //Send the string "PLAY" to all of the clients
        for(String client : server.getClients()) {
            try {
                DataOutputStream out = new DataOutputStream(server.getClientSocket(client).getOutputStream());
                out.writeUTF("PLAY");
            }
            catch (IOException e) {}
        }

        //Initial startup for the server@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        String move = "Game started!";

        //CREATE CARD GAME OBJECT
        GoFishGame cardGame = new GoFishGame(server.getClients().size(), server.getClients(), server.getClients().stream().map(clientName -> server.getClientSocket(clientName)).collect(Collectors.toList()), new File("resources\\cardlist.txt"));
        cardGame.assignDealer(server.getClients().iterator().next());
        Player focusPlayer = null;
        boolean win = false;
        boolean doesGoAgain = false;
        String winner = "Error";

        //4th Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Shuffle Cards
        cardGame.shuffleCards();
        //DEAL CARDS
        cardGame.dealCards();

        //TENTATIVELY
        //HAVE THE SERVER SEND LIST OF STRINGS TO PLAYERS FOR THEIR HAND OF CARDS

        while(server.gui.state.equals("server") && !win) {

            //Get the player that goes next
            //If doesGoAgain == false
            //		Get the next player for the turn
            //Else if doesGoAgain == true
            //		The Player gets to again as in their previous turn they got the card they queried for

            //The first turn will always have doesGoAgain = false
            if(!doesGoAgain)
                focusPlayer = cardGame.getPlayerQueue().nextPlayer();


            //Group 1@@@@@Message from Server to each Client@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            for(Player p: cardGame.getPlayerList()) {
                try {
                    DataOutputStream out = new DataOutputStream(p.getSock().getOutputStream());
                    //Adding in an extra first group to notify what kind of message is sent
                    //The sent message will be held in a string mes then a string array mess separated by semicolons ';'
                    //mess[0] = The last move played
                    //mess[1] = The client player's name
                    //mess[2] = The list of active cards | cards are delimited by ' '
                    //mess[3] = The list of inactive cards | cards are delimited by ' '
                    //mess[4] = The number of cards in the deck
                    //mess[5] = The number of active cards per each player | Players are delimited by ',' | cards are delimited by ' '
                    //mess[6] = Unique pairs held by each player | Players are delimited by ','  | cards are delimited by ' '
                    out.writeUTF(move+";"+focusPlayer.getTeamName()+";"+p.getCardListForUTF()+";"+cardGame.getAmtCardInDrawDeck()+";"+cardGame.getAmtCardsPerAHand()+";"+cardGame.getPairsPerHand());
                }
                catch (IOException e) {}
            }


            //Group 2@@@@@Receive the player's move the focusPlayer's client@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            try {
                DataInputStream in = new DataInputStream(focusPlayer.getSock().getInputStream());
                move = focusPlayer.getTeamName() + " played " + in.readUTF();
            }
            catch (IOException e) {
                move = focusPlayer.getTeamName() + " was skipped by server";
            }

            //Group 3@@@@@Apply changes to the model@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


            //TODO
            //Retrieve card value from message
            //Retrieve source player name
            //doesGoAgain = cardGame.queryPlayer(new Card("SA"), focusPlayer.getTeamName(), move);


            //###What is this???###
			/*boolean does = true;
			while(does) {
				try {
					DataInputStream in = new DataInputStream(focusPlayer.getSock().getInputStream());
					move = focusPlayer.getTeamName() + " played " + in.readUTF();
				}
				catch (IOException e) {
					move = focusPlayer.getTeamName() + " was skipped by server";
					does = doesGoAgain = false;
				}

				//CHECK MOVE
				boolean legal = cardGame.isLegalMove(focusPlayer, move);
				if(legal) {
					//LinkedList<Card> cards = focusPlayer.getActiveCards();
					//Card card = new Card("21", ""); //TODO search cards for request
					//Player source = focusPlayer; //TODO search playerList for requested
					//doesGoAgain = cardGame.queryPlayer(card.getVal(), focusPlayer, source);
					//does = false;
				}
			}*/




            //CHECK FOR WIN CONDITION
            win = (winner = cardGame.determineWinner()) != null;
        }

        //5th Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Re-send out everything one more time
        for(Player p: cardGame.getPlayerList()) {
            try {
                DataOutputStream out = new DataOutputStream(p.getSock().getOutputStream());
                out.writeUTF(move+";"+focusPlayer.getTeamName()+";"+p.getCardListForUTF()+";"+cardGame.getAmtCardInDrawDeck()+";"+cardGame.getAmtCardsPerAHand()+";"+cardGame.getPairsPerHand());
            }
            catch (IOException ignored) {}
        }


        //Send message to clients to go to win screen and display who won
        System.out.println("A winner is "+winner);
        //Send the string "PLAY" to all of the clients
        for(String client : server.getClients()) {
            try {
                DataOutputStream out = new DataOutputStream(server.getClientSocket(client).getOutputStream());
                out.writeUTF("Winner;"+winner);
            }
            catch (IOException ignored) {}
        }


    }
}
