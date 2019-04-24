package model;
/* @author Jacob */

import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/* Represents one of the people playing the game */
public class Player
{
/* Data */
	/* Name of the team that the player belongs to */
	private String teamName;

	/* Identifier marking the role the player has in the game */
	private String role;

	/* Cards the player possesses (both active and inactive) */
	private Hand hand;
	
	//Socket that the player is connected on
	private Socket playerSock;

/* Public methods */
	
	/* Constructor */
	public Player(String cTeamName, String cRole, Socket cSock)
	{
		teamName = cTeamName;
		role = cRole;
		hand = new Hand();
		playerSock = cSock;
	}

	public void addCard(Card card){
	    hand.addCard(card);
    }

	/* Adds all the cards in the list to the player's active cards */
	public void addCards(List<Card> cards)
	{
		hand.addCards(cards);
	}

	/* Removes all the cards in the list from the player's active cards
	 * and returns a list of all cards successfully removed */
	public List<Card> removeCards(List<Card> cards)
	{
		return hand.removeCards(cards);
	}
	
	/**
	 * Removes all cards with same value as 'value' and return a list with them
	 * @return List<Card> - list of cards with same value from hand
	 */
	public List<Card> shedPairs() {
		List<Card> ret = hand.checkMatches();
		hand.transferActiveToInactive(ret);
		return ret;
	}
	
	/**
	 * Returns the number of cards this player has
	 * @author Chris
	 * @return returns the number of cards in the players active hand
	 */
	public int getNumOfCards(){
		return hand.getNumActiveCards();
	}
	
	/**
	 * Returns a list of values of the pairs that are held by a player
	 * Each card value is separated by spaces
	 * Returns a 0 if the player has no pairs
	 * IE
	 * @return		A 2 3 8 1 K or 0
	 * @author Chris
	 */
	public String getPairs() 
	{
		if(hand.getNumInactiveCards() == 0)
			return "0";
		else
		{
			return hand.findMatches();
		}
	}
	
	/**
	 * Returns this player's role
	 * @author Chris
	 */
	public void assignRole(String newRole){
		role = newRole;
	}
	public String getRole(){
		return role;
	}
	public String getTeamName(){
		return teamName;
	}
	public Socket getSock(){
		return playerSock;
	}
	
	/**
	 * Checks to see if player has any cards of type category
	 * @param suit - suit of card to check for
	 * @return List<Card> which is not empty if a card of the same suit has been found, and empty if no card was found
	 */
	public List<Card> getCardsOfSuit(Card.Suit suit) {
		return hand.getActiveCards().stream().filter(card -> card.getSuit().equals(suit)).collect(Collectors.toList());
	}

	/**
	 * Checks to see if player has any cards of type category
	 * @param value - suit of card to check for
	 * @return Optional<Card> which is non-null if a card of the same suit has been found, and null if no card was found
	 */
	public List<Card> getCardsOfValue(Card.Value value) {
		return hand.getActiveCards().stream().filter(card -> card.getVal().equals(value)).collect(Collectors.toList());
	}

	/* Transfers all the cards in the list from the player's active cards
	 * to their inactive cards and returns a list of all cards successfully
	 * transferred */
	public List<Card> transferActiveToInactive(List<Card> cards)
	{
		return hand.transferActiveToInactive(cards);
	}

	/* Transfers all the cards in the list from the player's inactive cards
	 * to their active cards and returns a list of all cards successfully
	 * transferred */
	public List<Card> transferInactiveToActive(List<Card> cards)
	{
		return hand.transferInactiveToActive(cards);
	}

	/* Used to perform game-specific actions that go beyond
	 * manipulating one's cards. Returns result of action as a String */
	public String takeAction(String action)
	{
		String result = "";
		return result;
		/* TODO */
	}

/* Getters */
	public List<Card> getActiveCards(){
		return hand.getActiveCards();
	}

	public List<Card> getInactiveCards(){
		return hand.getInactiveCards();
	}
	/**
	 * The players card list uses 3 delimiters
	 *	The ';' delimits the active list form the inactive list. ActiveCards;InactiveCards
	 *	The ',' delimits the cards in a list from each other. Card1,Card2,Card3
	 * @author Chris
	 * @return
	 */
	public String getCardListForUTF()
	{
		StringBuilder cardList = new StringBuilder();
		
		if(getActiveCards().size()>0)
		{
			for(Card card: getActiveCards())
				cardList.append(card.toString()).append(",");
			cardList.setCharAt(cardList.lastIndexOf(","), ';');
		}
		else
			cardList.append(" ;");
		
		if(getInactiveCards().size()>0)
		{
			for(Card card: getInactiveCards())
				cardList.append(card.toString()).append(",");
			cardList.deleteCharAt(cardList.lastIndexOf(","));
		}
		else
			cardList.append(' ');
		
		return cardList.toString();
	}

}
