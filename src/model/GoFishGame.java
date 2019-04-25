package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GoFishGame extends CardGame {

	private GoFishQueue playerQueue;

	public GoFishGame(int numOfPlayers, ArrayList<String> playerNames, ArrayList<Socket> clientSocks, File cardList) {
		super(numOfPlayers, playerNames, clientSocks, cardList);

		playerQueue = sortPlayersInPlayOrder();
	}

	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO
		return true;
	}

	/**
	 * Checks if target has card; if true, transfer all of that type of card from
	 * target to source, otherwise source draws one card
	 * 
	 * @param value
	 *            - Value of card source is testing target with
	 * @param target
	 *            - Player asking for cards
	 * @param source
	 *            - Player being asked
	 * @return true if target has card type, false if not
	 */
	public boolean queryPlayer(Card.Value value, Player target, Player source) 
	{
		//Check to see if the source has cards with the same value
		List<Card> query = source.getCardsOfValue(value);
		boolean doesGoAgain;
		
		//If the source has those cards
		if (!query.isEmpty()) {
			transferCardsFromOther(query, target, source);	//Move the cards from the source to the target
			lostCards(source);								//Check if the source needs to gain a new hand or remove them from play
			doesGoAgain = true;
		}
		//If the source does not have those cards
		else {
			if(!cardDeck.getCards().isEmpty())			//Check if the deck is not empty
				target.addCard(cardDeck.takeCard()); 	// target draws one card if source does not contain any cards of
			doesGoAgain = false;
		}
		
		target.checkBooks();							//Checks if the target has a 4 of a kind pair. Sends those cards to the inactiveCards
		lostCards(target);
		return doesGoAgain;
	}

	/**
	 * Move all cards with value [value] from source to target
	 * 
	 * @param cards
	 *            - List of cards to transfer
	 * @param target
	 *            - Player asking for cards
	 * @param source
	 *            - Player being asked
	 */
	public void transferCardsFromOther(List<Card> cards, Player target, Player source) {
		target.addCards(source.removeCards(cards));
	}

	/**
	 * Checks if the player has any active cards left
	 * If they don't, check if the draw deck has any cards left to draw
	 * If the draw deck has no more cards left, then remove the player from the queue
	 * Note: May perform Recursion
	 * @param player
	 */
	public void lostCards(Player player) 
	{
		//Check if the player lost all their cards, if not then nothing else to do. But if so
		if(player.getNumOfCards() == 0)
		{
			//If there are cards left in the deck
			if(!cardDeck.isEmpty())
			{
				//Refill the player's hand with up to the amount they started with at the beginning of the game
				player.addCards(cardDeck.refillHand(getStartingHandSize()));
				//Since they are recieving cards we must check if they got a 4 of a kind
				player.checkBooks();
				//And if they have moved cards from the active cards to the inactive cards we must check if they got rid of their whole hand
				lostCards(player);
			}
			//Else if there are no cards left in the deck
			else
			{
				//Remove that player
				playerQueue.dequeue(player);
			}
		}
		
	}

	/**
	 * Checks to see if the game is over, which is if no player has any active cards 
	 * left in their hands.
	 * This is done by checking the size of the GoFishQueue
	 * Returns the name of the player who won.
	 * Or
	 * Returns null if the queue isn't empty
	 * @author Chris
	 * @return The Player.getTeamName() if a winner is found or null if no player wins
	 */
	public String determineWinner() 
	{
		if(playerQueue.getSize() > 0)
			return null;
		else
		{
			//int[] totals = new int[players.length];	//Create an int counter for each player that played the game
			int currentPairs;
			int mostPairs = -1;
			int mostIndex = -1;
			String temp;
			//Get each player's total number of pairs and determine which player has the most
			for(int i = 0; i < players.length; ++i)
			{
				temp = players[i].getPairs();//Check to make sure if this player got any pairs
				if(temp.length() == 1) //If the player has no pairs, the string is " "
					currentPairs = 0;
				else
					currentPairs = temp.split(" ").length;
				
				if(currentPairs > mostPairs)
				{
					mostPairs = currentPairs;
					mostIndex = i;
				}
			}
			
			return players[mostIndex].getTeamName();
		}
	}

	public GoFishQueue sortPlayersInPlayOrder() {
		// CLIENTSOCKS AND CLIENTLABELS are automatically sorted within the playerQueue
		// as they are part of the model.Player object

		int dealerNum; // Track the index of the dealer
		// Index through array until dealer is found, if not then stop at end of list
		for (dealerNum = 0; dealerNum < players.length && !players[dealerNum].getRole().equals("Dealer"); dealerNum++)
			;

		// Move number to next in list as dealer doesn't usually go first
		dealerNum = (dealerNum) % players.length;
		// Create the playerQueue
		GoFishQueue playOrder = new GoFishQueue();

		for (int i = 0; i < players.length; i++) // For each player
			playOrder.enqueue(players[(dealerNum + i) % players.length]); // Starting at the dealer, add them to the
																			// queue

		return playOrder; // Return the queue
	}

	/**
	 * @author Chris
	 * Returns the number of cards in each player's hand
	 * It returns a comma separated list of integers where each one left to right 
	 * represents the cards in that respective player's hand.
	 * IE
	 * @return	numOfCardsForPlayer1,numOfCardsForPlayer2,numOfCardsForPlayer3,...
	 */
	public String getAmtCardsPerAHand() 
	{
		StringBuilder cardsPerHand = new StringBuilder();
		for(Player p: players)
			cardsPerHand.append(p.getNumOfCards()+",");
		cardsPerHand.setCharAt(cardsPerHand.lastIndexOf(","), ';');
		return cardsPerHand.toString();
	}

	/**
	 * @author Chris
	 * Returns the pairs in each player's hand
	 * It returns a comma separated list of character sequences where each one left to right 
	 * represents the pairs with that respective player.
	 * The characters for each player are separated by spaces
	 * A player
	 * IE
	 * @return	DA S2 A3 D8 DT DK,A4 D5," ",...
	 * " " = a single space
	 */
	public String getPairsPerHand() 
	{
		StringBuilder pairsPerHand = new StringBuilder();
		for(Player p: players)
			pairsPerHand.append(p.getPairs()+",");
		pairsPerHand.setCharAt(pairsPerHand.lastIndexOf(","), ';');
		return " ";
	}

	/**
	 * @author Chris
	 * Returns the number of cards left in the deck
	 * @return
	 */
	public int getAmtCardInDrawDeck() 
	{
		return cardDeck.getNumOfCards();
	}

	/**
	 * Returns the queue of players in the game who still have cards to play. 
	 * @return The queue of players
	 */
	public GoFishQueue getPlayerQueue()
	{
		return playerQueue;
	}
	
	/**
	 * Returns the full list of players regardless of the number of cards they have left
	 * @return The array of Players
	 */
	public Player[] getPlayerList()
	{
		return players;
	}
}