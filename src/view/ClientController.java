package view;

import javafx.application.Platform;
import model.Card;
import model.GoFishGame;
import model.Hand;
import model.Player;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.List;

public class ClientController {

	private static final int SERVER_PORT = 0;
	ClientGUI gui;
	
	//server stuff
	ServerSocket server;//the 'server' that will wait for a client socket to connect
	HashMap<String, Socket> clients;
	ServerThread serverThread;
	boolean isServer;
	
	//client stuff
	Socket serverSock; //the client
	ClientThread clientThread;
	DataOutputStream outputStream;

	public ClientController() {
		gui = null;
		
		server = null; //the 'server' that will wait for a client socket to connect
		clients = new HashMap<>();
		serverThread = new ServerThread(this);
		isServer = false;
		
		serverSock = null; //the client
		clientThread = new ClientThread(this);
	}
	
	
	String setupHost() {
		String hostIP = null;

		//create host
		boolean success = false;
		int attempts = 0;//keeps track of attempts to establish connection
		while(!success && attempts < 10){//tries ten times to create the server
			/*DEBUG*/System.out.println("Trying to make host");
			attempts++;
			try{
				server = new ServerSocket(SERVER_PORT);//throws IOException if port is taken
				success = true;
				hostIP = server.getInetAddress().getHostAddress() +":"+ server.getLocalPort();
				
				//put ip in clipboard to make my life easier
				StringSelection data = new StringSelection(hostIP);
				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				cb.setContents(data, data);
				
			}
			catch(IOException e){
				//System.out.println("Could not create ServerSocket at port "+port);
			}
		}
		
		if(!success) {
			hostIP = "Unable to establish host";
		}
		
		/*DEBUG*/System.out.println("Made host: "+hostIP);
		
		return hostIP;
	}

	boolean isHosting(){
		return server != null;
	}

	String getServerAddress(){
		return server.getInetAddress().getHostAddress() + ":" + server.getLocalPort();
	}

	String connectToHost(String addressStr, String name) {
		try {
			serverSock = new Socket();
			String[] addressSplit = addressStr.split(":", 0);
			InetSocketAddress address = new InetSocketAddress(addressSplit[0], Integer.parseInt(addressSplit[1]));
			serverSock.connect(address, 5000);
			outputStream = new DataOutputStream(serverSock.getOutputStream());
			outputStream.writeUTF(name);
			return "Connected!";
		}
		catch(UnknownHostException e){
			return "Could not find host!";
		}
		catch(NumberFormatException e){
			return "Invalid host address!";
		}
		catch(SocketTimeoutException e){
			return "Connection attempt timed out! Try again";
		}
		catch(IOException e){
			return "Error resolving host!";
		}
	}
	
	void closeSocks(String state) {
		if(state.equals("hosting")) {
			try{
				server.close();
			}
			catch(Exception ignored){}
			server = null;//the 'server' that will wait for a client socket to connect
			for(Socket s : clients.values()) {
				try{
					s.close();
				}
				catch(Exception ignored){}
			}
			clients.clear();
		}
		else if(state.equals("lobby")) {
			try{
				serverSock.close();
			}
			catch(Exception ignored){}
			serverSock = null;
		}
	}
	
	
	void writeToServer(String mes) throws IOException {
		outputStream.writeUTF(mes);
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

class ClientThread extends Thread{
	
	ClientController game;
	LinkedList<Card> tempList;
	String[] mess;
	
	public ClientThread(ClientController game) {
		this.game = game;
		tempList = new LinkedList<Card>();
		mess = null;
	}
	
	public void run() {
		
		/*DEBUG*/System.out.println(getId()+": Thread started");
		
		while(!game.gui.state.equals("main")) {
		
			try {
				DataInputStream in = new DataInputStream(game.serverSock.getInputStream());
				//*DEBUG*/System.out.println(getId()+": Wating for message from server");
				String mes = in.readUTF();

				//*DEBUG*/System.out.println(getId()+": got from server: "+mes);
				//Client State 1@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				if((game.gui.state.equals("lobby") || game.gui.state.equals("hosting")) && mes.length()>3 && mes.substring(0, 4).equals("PLAY")){
					//update gui
					Platform.runLater(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					   public void run() {
						   //Initiailize this client's variables
						   game.gui.yourID = Integer.parseInt(mes.substring(5,6)); //Set the playerID
						   game.gui.yourCards = new Hand();
						   game.gui.deckCount = Integer.parseInt(mes.substring(7));	//Temporarily putting the amount of players here
						   game.gui.cardCounts = new int[game.gui.deckCount];
						   game.gui.playerPairs = new List[game.gui.deckCount];
						   for(int i = 0; i < game.gui.deckCount; ++i)
							   game.gui.playerPairs[i] = new ArrayList<Card>();

						   //Send this client to the game state
						   game.gui.game();
					   }
					});
				}
				//Client State 2@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				else if(game.gui.state.equals("lobby") || game.gui.state.equals("hosting")) {
					Platform.runLater(() -> System.out.println(mes));
				}
				//Client State 3@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				else if(game.gui.state.equals("game")){
					//&&&&&Part 1&&&&&
					//Check to see if we have a valid message that has semicolons
					if(mes.indexOf(';') == -1) continue;
					

					//Split the in message
					/*String[]*/ mess = mes.split(";", 0);
					
					//&&&&Part 2&&&&&
					Platform.runLater(new Runnable() {
					   @Override
					   public void run() {
						   
						   //Update players messages about the previous move and whose turn it is
						 
						   //Tells the Client what move was made
						   System.out.println(mess[1]);
							   
						   //Determines if this Client is the one to go next
						   if(mess[2].equals(game.gui.yourName)) {
							   System.out.println("It's your turn");
							   //game.gui.root.getChildren().add(game.gui.playButton);
						   }
						   else {
							   System.out.println("It's " + mess[2] + "'s turn");
						   }

						   System.out.println(game.gui.yourCards == null);
							   
						   //Giving players their cards
						   game.fillHand(mess[2], mess[3]);

						   //Test if client got the message
						   System.out.println(mes);
						   
						   //Update each Client how many cards are in the Draw Deck
						   //Update each client on how many card each player has
						   game.setCardCounts(mess[4], mess[5]);

						   //Update each client on which matches each player has
						   game.setPlayerPairs(mess[6]);
					   }
					});
				}
				//Client State 4@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				//Used with Server stage 5
				//Making changes to the clients
				//Sending them to the win screen and closing down all sockets
				else if(mes.length()>5 && mes.substring(0, 6).equals("Winner"))
				{
					
				}
				
			} catch (IOException e) {
				
			}
		}
		/*DEBUG*/System.out.println(getId()+": Thread ended");
		
	}//end run
}//end ServerListener

class ServerThread extends Thread{
	
	ClientController game;
	
	public ServerThread(ClientController game) {
		this.game = game;
	}
	
	public void run() {
		
		try {
			game.server.setSoTimeout(1000);//sets time to wait for client to 1 second
		}
		catch(SocketException e){//thrown if the socket is bad
			Platform.runLater(() -> System.out.println("Unable to establish host"));
		}
		//1st Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		while(game.gui.state.equals("hosting")) {
			try {
				Socket clientSocket = game.server.accept();
				/*DEBUG*/System.out.println("Client Connected");
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				String clientName = in.readUTF();
				game.clients.put(clientName, clientSocket);
				//game.clientLabels.add(clientName);
				//game.clientSocks.add(clientSocket);
				/*DEBUG*/System.out.println(clientName);

			}
			catch(SocketTimeoutException e){//no clients connect within the timeout delay
				//System.out.println("Nobody wanted to connect.");
				//That's fine, we'll just keep waiting
			}
			catch(IOException e){
				//System.out.println("IOException during accept()");
				//oh well, won't have that client
			}
			catch(NullPointerException e){
				//System.out.println("IOException during accept()");
				//oh well, won't have that client
			}

			//update gui
			StringBuilder names = new StringBuilder();
			for(Map.Entry<String, Socket> client: game.clients.entrySet()){
				if(client.getValue().isClosed()){
					game.clients.remove(client.getKey());
				}
				else{
					names.append(client.getKey()).append("\n");
				}
			}
			for(Map.Entry<String, Socket> client: game.clients.entrySet()) {
				try {
					DataOutputStream out = new DataOutputStream(client.getValue().getOutputStream());
					out.writeUTF(names.toString());
				}
				catch (IOException ignored) {}
			}

		}
		//2nd Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		//Don't move past if this thread's client hasn't gone in game yet
		if(!game.gui.state.equals("game")) return;
		
		//3rd Stage@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		//Temporarily reset the state to hosting so the server's client can recieve the message properly
		game.gui.state = "hosting";

		//Send the string "PLAY" to all of the clients
		for(Socket client : game.clients.values()) {
			try {
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				out.writeUTF("PLAY");
			}
			catch (IOException e) {}
		}
		
		//Initial startup for the game@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		String move = "Game started!";
		
		//CREATE CARD GAME OBJECT
		GoFishGame cardGame = new GoFishGame(game.clients.size(), new ArrayList<>(game.clients.keySet()), new ArrayList<>(game.clients.values()), new File("resources\\cardlist.txt"));
		cardGame.assignDealer(game.clients.keySet().iterator().next());
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
		
		while(game.gui.state.equals("game") && !win) {
			
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
		for(Socket client : game.clients.values()) {
			try {
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				out.writeUTF("Winner;"+winner);
			}
			catch (IOException ignored) {}
		}
		
		
	}
}//end ServerListener
