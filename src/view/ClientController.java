package view;

import model.Card;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class ClientController {

	private static final int SERVER_PORT = 0;
	ClientGUI gui;

    enum ClientState{
	    UNINITIALIZED, LOBBY, GAME
    }

	private Socket serverSock;
	private ClientState state;
    private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private List<String> players;

	ClientController(ClientGUI gui) {
	    this.gui = gui;
	    state = ClientState.UNINITIALIZED;
	    players = new ArrayList<>();
	}

	boolean connectToHost(String addressStr, String name) {
		try {
			serverSock = new Socket();
			String[] addressSplit = addressStr.split(":");
			InetSocketAddress address = new InetSocketAddress(addressSplit[0], Integer.parseInt(addressSplit[1]));
			serverSock.connect(address, 5000);
            state = ClientState.LOBBY;
			outputStream = new DataOutputStream(serverSock.getOutputStream());
            inputStream = new DataInputStream(serverSock.getInputStream());
			writeToServer(name);
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
	
	void closeSocks(String state) {
        try{
            serverSock.close();
        }
        catch(Exception ignored){}
        serverSock = null;
	}

    public ClientState getState() {
        return state;
    }

    String getServerAddress(){
        if(serverSock == null) return null;
        return serverSock.getInetAddress().getHostAddress() + ":" + serverSock.getLocalPort();
    }

    List<String> getPlayers() {
        return players;
    }

    boolean removePlayer(String player){
	    return players.remove(player);
    }

    void addPlayer(String name){
	    players.add(name);
    }
	
	void writeToServer(String mes) throws IOException {
		outputStream.writeUTF(mes);
	}

	String readFromServer() throws IOException {
	    return inputStream.readUTF();
    }
	
	
	//#####Transform Server Info to Client Method#####

	/**
	 * Sets the Client's hand with 2 Strings each in the form
	 * card1 card2 card3 ...
	 * Does not set the hand lists if a respective string is empty
	 * @param activeList - A list of Cards or " "
	 * @param inactiveList - A list of Cards or " "
	 */
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
	}

	/**
	 * Creates a List object out of String in the format of a list of cards separated by spaces
	 * @param cards card1 card2 card3 ...
	 * @return A List object of those cards
	 */
	List<Card> recreateCardList(String cards)
	{
		List<Card> cardList = new LinkedList<>();

		for(String card: cards.split(" "))
			cardList.add(new Card(card));

		return cardList;
	}
}

