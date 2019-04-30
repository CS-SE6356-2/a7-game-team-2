package main;

import javafx.application.Platform;
import model.Card;
import model.Player;
import view.ClientGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController {
	ClientGUI gui;

    enum ClientState{
	    UNINITIALIZED, LOBBY, GAME
    }

	private Socket serverSock;
	private ClientState state;
    private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private List<String> players;
	private Player thisPlayer;

	ClientController(ClientGUI gui) {
	    this.gui = gui;
	    state = ClientState.UNINITIALIZED;
	    players = new ArrayList<>();
	}

	public boolean connectToHost(String addressStr, String name) {
		try {
			serverSock = new Socket();
			String[] addressSplit = addressStr.split(":");
			InetSocketAddress address = new InetSocketAddress(addressSplit[0], Integer.parseInt(addressSplit[1]));
			serverSock.connect(address, 5000);
            state = ClientState.LOBBY;
			outputStream = new DataOutputStream(serverSock.getOutputStream());
            inputStream = new DataInputStream(serverSock.getInputStream());
			writeToServer(name);
			thisPlayer = new Player(name, "", serverSock);
            ClientThread clientThread = new ClientThread(this);
            clientThread.start();
			return true;
		}
		catch(UnknownHostException e){
		    gui.showAlert("Could not find host");
			return false;
		}
		catch(NumberFormatException e){
			gui.showAlert("Invalid host address");
			return false;
		}
		catch(SocketTimeoutException e){
			gui.showAlert("Connection attempt timed out! Please try again");
			return false;
		}
		catch(IOException e){
			gui.showAlert("Error resolving host");
			return false;
		}
	}

	void startGame() {
	    try{
	        System.out.println("Start Game");
            state = ClientState.GAME;
            Platform.runLater(() -> {
                try {
                    gui.startGame();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<String> hand = new ArrayList<>(Arrays.asList(readFromServer().split(":")));
            if(!hand.remove(0).equals(ServerMessage.DEAL_HAND.toString())) throw new IllegalStateException();
            thisPlayer.addCards(recreateCardList(hand));
            Platform.runLater(() -> {
                try {
                    gui.updateGame();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            gameLoop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameLoop(){
	    try{
	        String[] message;
            for(message = readFromServer().split(":"); !message[0].equals(ServerMessage.ANNOUNCE_WINNER.toString()); message = readFromServer().split(":")){
                if(message[0].equals(ServerMessage.START_TURN.toString())){
                    Platform.runLater(() -> gui.displayTurn());
                }
                else if(message[0].equals(ServerMessage.QUERY_RESPONSE.toString())){
                    System.out.println(message[0]);
                }
                else if(message[0].equals(ServerMessage.QUERY.toString())){
                    thisPlayer.removeCards(thisPlayer.getCardsOfValue(Card.Value.parseValue(message[1])));
                }
                Platform.runLater(() -> {
                    try {
                        gui.updateGame();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
            System.out.println("Winner is " + message[1]);
            closeSocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendQuery(String c, String target){
        try {
            String query = ServerMessage.QUERY.toUTF() +
                    c + ":" +
                    thisPlayer.getName() + ":" +
                    target;
            writeToServer(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void closeSocks() {
        try{
            serverSock.close();
        }
        catch(Exception ignored){}
        serverSock = null;
	}

    ClientState getState() {
        return state;
    }

    public String getServerAddress(){
        if(serverSock == null) return null;
        return serverSock.getInetAddress().getHostAddress() + ":" + serverSock.getLocalPort();
    }

    public Player getPlayer(){
	    return thisPlayer;
    }

    public List<String> getPlayers() {
        return players;
    }

    boolean removePlayer(String player){
	    return players.remove(player);
    }

    void addPlayer(String name){
	    players.add(name);
    }
	
	private void writeToServer(String mes) throws IOException {
	    System.out.println("Client Sending Message: " + mes);
		outputStream.writeUTF(mes);
	}

	String readFromServer() throws IOException {
	    String ret = inputStream.readUTF();
	    System.out.println("Client Received Message: " + ret);
	    return ret;
    }
	

    /*
	/**
	 * Sets the Client's hand with 2 Strings each in the form
	 * card1 card2 card3 ...
	 * Does not set the hand lists if a respective string is empty
	 * @param activeList - A list of Cards or " "
	 * @param inactiveList - A list of Cards or " "
	 */
	/*
	void fillHand(String activeList, String inactiveList)
	{
		if(!activeList.equals(" "))
			gui.yourCards.setActiveCards(recreateCardList(activeList));
		if(!inactiveList.contentEquals(" "))
			gui.yourCards.setInactiveCards(recreateCardList(inactiveList));
	}

	void setCardCounts(String deckCount, String playerCardCounts)
	{
		gui.deckCount = Integer.parseInt(deckCount);
		int i = 0;
		for(String count: playerCardCounts.split(","))
			gui.cardCounts[i++] = Integer.parseInt(count);
	}

	void setPlayerPairs(String playerPairs)
	{
		int i = 0;
		for(String cardList: playerPairs.split(","))
		{
			if(!cardList.equals(" "))
			{
				gui.playerPairs[i].clear();
				for(String card: cardList.split(" "))
					gui.playerPairs[i].add(new Card(card));
			}
			++i;
		}
	}*/

	/**
	 * Creates a List object out of String in the format of a list of cards separated by spaces
	 * @param cards card1 card2 card3 ...
	 * @return A List object of those cards
	 */
    private List<Card> recreateCardList(List<String> cards) {
	    return cards.stream().map(Card::new).collect(Collectors.toList());
	}
}

