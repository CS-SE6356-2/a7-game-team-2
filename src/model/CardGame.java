package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CardGame
{
	Player[] players;			//Holds the data for each player
	Deck cardDeck;				//Holds the information for each card

	public CardGame(int numOfPlayers, List<String> playerNames, List<Socket> clientSocks)
	{
		players = new Player[numOfPlayers];		//Create a list of Players
		cardDeck = new Deck();			//Create the deck of cards
		
		//Create Players
		createPlayers(playerNames, clientSocks);
	}

	public CardGame(int numOfPlayers, ArrayList<String> playerNames, ArrayList<Socket> clientSocks,
					File cardList)
	{
		players = new Player[numOfPlayers];		//Create a list of Players
		cardDeck = new Deck(cardList);			//Create the deck of cards

		//Create Players
		createPlayers(playerNames, clientSocks);
	}
	
	/**
	 * 
	 */
	public void dealCards()
	{
		for(int i = 0; i < getStartingHandSize(); i++){
			for (Player player : players) {
				player.addCard(cardDeck.takeCard());
			}
		}
	}

	int getStartingHandSize() {
		if(players.length < 4) return 7;
		else return 5;
	}
	public void shuffleCards(){
		cardDeck.shuffle();
	}
	private void createPlayers(List<String> playerNames, List<Socket> clientSocks)
	{
		for(int i = 0; i < players.length; i++)
		{
			players[i] = new Player(playerNames.get(i),"Solo", clientSocks.get(i));
		}
	}
	
	/**
	 * Sorts the list of players initially in a game by finding the dealer, adding them and the other players into a circular linked list called playerQueue
	 * @author Chris
	 * @return playerQueue
	 */
	public PlayerQueue sortPlayersInPlayOrder()
	{
		//CLIENTSOCKS AND CLIENTLABELS are automatically sorted within the playerQueue as they are part of the model.Player object
		
		int dealerNum;	//Track the index of the dealer
		 //Index through array until dealer is found, if not then stop at end of list
		for(dealerNum = 0;dealerNum < players.length && !players[dealerNum].getRole().equals("Dealer"); dealerNum++);
		
		//Move number to next in list as dealer doesn't usually go first
		dealerNum = (dealerNum)%players.length;
		//Create the playerQueue
		PlayerQueue playOrder = new PlayerQueue();
		
		for(int i = 0; i < players.length; i++)							//For each player
			playOrder.enqueue(players[(dealerNum+i)%players.length]);	//Starting at the dealer, add them to the queue
		
		return playOrder;	//Return  the queue
	}
	/**
	 * Assigns the given player as the new dealer.
	 * @author Chris
	 * @param newDealer
	 * @return True if a new dealer has been assigned | False if not
	 */
	public boolean assignDealer(String newDealer)
	{
		for(Player p: players)
			if(p.getTeamName().equals(newDealer))
			{
				p.assignRole("Dealer");
				return true;
			}
		return false;
	}
	
	private int getMatchValue(List<Card> match)
	{
		//TODO
		return 21;
	}

	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO Extend Game Logic here,
		//      i.e. what kind of card
		//      or action did they make
		return true;
	}
	
	/**
	 * Checks to see if a player has met the winning conditions
	 * @author Chris
	 * @return
	 */
	public boolean checkWinCondition(Player focusPlayer, String move)
	{
		//TODO extend into a specific game type (set of rules)
		if(focusPlayer.getNumOfCards() == 0)
			return true;
		return false;
	}
	
	/**
	 * 
	 */
	public boolean queryPlayer(String sourceName, String cardValue, String targetName) {
		
		
		return false;
	}

}