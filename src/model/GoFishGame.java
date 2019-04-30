package model;

import java.io.File;
import java.net.Socket;
import java.util.*;

public class GoFishGame extends CardGame {

	public GoFishGame(List<String> playerNames, List<Socket> clientSocks, File cardList) {
		super(playerNames, clientSocks, cardList);
	}

	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO
		return true;
	}

	public boolean queryPlayer(String cardSymbol, Player target, String sourceName) 
	{
		return queryPlayer(Card.symbolToValue(cardSymbol), target, getPlayer(sourceName));
	}
	
	/**
	 * Checks if target has card; if true, transfer all of that type of card from
	 * target to source, otherwise source draws one card
	 * 
	 * @param value Value of card source is testing target with
	 * @param source Player asking for cards
	 * @param target Player being asked
	 * @return true if source has card type, false if not
	 */
	public boolean queryPlayer(Card.Value value, Player source, Player target)
	{
		//Check to see if the target has cards with the same value
		List<Card> query = target.getCardsOfValue(value);
		boolean doesGoAgain;
		
		//If the target has those cards
		if (!query.isEmpty()) {
			transferCardsFromOther(query, source, target);	//Move the cards from the target to the source
			lostCards(target);								//Check if the target needs to gain a new hand or remove them from play
			doesGoAgain = true;
		}
		//If the target does not have those cards
		else {
			if(!cardDeck.getCards().isEmpty())			//Check if the deck is not empty
				source.addCard(cardDeck.takeCard()); 	// source draws one card if target does not contain any cards of
			doesGoAgain = false;
		}
		source.checkBooks();							//Checks if the source has a 4 of a kind pair. Sends those cards to the inactiveCards
		lostCards(source);
		return doesGoAgain;
	}

	/**
	 * Move all cards in given list from target to source
	 * 
	 * @param cards List of cards to transfer
	 * @param source Player asking for cards
	 * @param target Player being asked
	 */
	private void transferCardsFromOther(List<Card> cards, Player source, Player target) {
		source.addCards(target.removeCards(cards));
	}

	/**
	 * Checks if the player has any active cards left
	 * If they don't, check if the draw deck has any cards left to draw
	 * If the draw deck has no more cards left, then remove the player from the queue
	 * Note: May perform Recursion
	 * @param player
	 */
	void lostCards(Player player)
	{
		//Check if the player lost all their cards, if not then nothing else to do. But if so
		if(player.getNumOfCards() == 0)
		{
			//If there are cards left in the deck
			if(!cardDeck.isEmpty())
			{
				//Refill the player's hand with up to the amount they started with at the beginning of the game
				player.addCards(cardDeck.refillHand(getStartingHandSize()));
				//Since they are receiving cards we must check if they got a 4 of a kind
				player.checkBooks();
				//And if they have moved cards from the active cards to the inactive cards we must check if they got rid of their whole hand
				lostCards(player);
			}
			//Else if there are no cards left in the deck
			else
			{
				//Remove that player
				queue.remove(player);
			}
		}
		
	}

	/**
	 * Checks to see if the game is over, which is if no player has any active cards 
	 * left in their hands.
	 * This is done by checking the size of the PlayerQueue
	 * Returns the name of the player who won.
	 * Or
	 * Returns null if the queue isn't empty
	 * @author Chris
	 * @return The Player.getName() if a winner is found or null if no player wins
	 */
	public Player determineWinner() {
		if(queue.getSize() > 0)
			return null;
		else {
			//int[] totals = new int[players.length];	//Create an int counter for each player that played the game
			int currentPairs;
			int mostPairs = -1;
			int mostIndex = -1;
			String temp;
			//Get each player's total number of pairs and determine which player has the most
			for(int i = 0; i < players.size(); ++i) {
				temp = players.get(i).getPairs(players.get(i).getInactiveCards());//Check to make sure if this player got any pairs
				if(temp.length() == 1) //If the player has no pairs, the string is " "
					currentPairs = 0;
				else
					currentPairs = temp.split(" ").length;
				
				if(currentPairs > mostPairs) {
					mostPairs = currentPairs;
					mostIndex = i;
				}
			}
			return players.get(mostIndex);
		}
	}

	/**
	 * @author Chris
	 * Returns the number of cards in each player's hand
	 * It returns a comma separated list of integers where each one left to right 
	 * represents the cards in that respective player's hand.
	 * IE
	 * @return	numOfCardsForPlayer1,numOfCardsForPlayer2,numOfCardsForPlayer3,...
	 */
	public String getAmtCardsPerAHand() {
		StringBuilder cardsPerHand = new StringBuilder();
		for(Player p: players)
			cardsPerHand.append(p.getNumOfCards()).append(",");
		cardsPerHand.deleteCharAt(cardsPerHand.lastIndexOf(","));
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
	public String getPairsPerHand() {
		StringBuilder pairsPerHand = new StringBuilder();
		for(Player p: players)
			pairsPerHand.append(p.getPairs(p.getInactiveCards())).append(",");
		pairsPerHand.deleteCharAt(pairsPerHand.lastIndexOf(","));
		return pairsPerHand.toString();
	}

	/**
	 * @author Chris
	 * Returns the number of cards left in the deck
	 * @return
	 */
	public int getAmtCardInDrawDeck() {
		return cardDeck.getNumOfCards();
	}

	/**
	 * Returns the queue of players in the game who still have cards to play. 
	 * @return The queue of players
	 */
	PlayerQueue getPlayerQueue() {
		return queue;
	}
	
	/**
	 * Returns the full list of players regardless of the number of cards they have left
	 * @return The array of Players
	 */
	public List<Player> getPlayerList() {
		return players;
	}
	
	public Player getPlayer(String name)
	{
		return players.stream().filter(player -> player.getName().equals(name)).findFirst().orElse(null);
	}
	
}