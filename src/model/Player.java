package model;/* @author Jacob */

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

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

	/* Adds all the cards in the list to the player's active cards */
	public void addCards(LinkedList<Card> cards)
	{
		hand.addCards(cards);
	}

	/* Removes all the cards in the list from the player's active cards
	 * and returns a list of all cards successfully removed */
	public LinkedList<Card> removeCards(LinkedList<Card> cards)
	{
		return hand.removeCards(cards);
	}
	
	/**
	 * Removes all cards with same value as 'value' and return a list with them
	 * @param value - value of card to shed
	 * @return list of cards with same value from hand
	 */
	public List<Card> shedCards(String value) {
		
		LinkedList<Card> matches = new LinkedList<>();
		
		for(int i = 0; i < hand.getActiveCards().size(); i++) {
			if(hand.getActiveCards().get(i).getVal().equals(value))
				matches.add(hand.getActiveCards().get(i));
		}
		
		return hand.removeCards(matches);
	}
	
	/**
	 * Returns the number of cards this player has
	 * @author Chris
	 * @return
	 */
	public int getNumOfCards() {return hand.getNumOfCards();}
	/**
	 * Returns this player's role
	 * @author Chris
	 * @return
	 */
	public void assignRole(String newRole) {
		role = newRole;
	}
	public String getRole() {return role;}
	public String getTeamName() {return teamName;}
	public Socket getSock() {return playerSock;}
	
	/**
	 * Checks to see if player has any cards of type category
	 * @param value - suit of card to check for
	 * @return true if player hand contains card with same category, false if not
	 */
	public boolean hasCardType(String value) {
		List<Card> cards = hand.getActiveCards();
		for(Card c : cards) {
			if(c.getVal().equals(value))
				return true;
		}
		
		return false;
	}

	/* Transfers all the cards in the list from the player's active cards
	 * to their inactive cards and returns a list of all cards successfully
	 * transferred */
	public LinkedList<Card> transferActiveToInactive(LinkedList<Card> cards)
	{
		return hand.transferActiveToInactive(cards);
	}

	/* Transfers all the cards in the list from the player's inactive cards
	 * to their active cards and returns a list of all cards successfully
	 * transferred */
	public LinkedList<Card> transferInactiveToActive(LinkedList<Card> cards)
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
	public List<Card> getActiveCards()
	{
		return hand.getActiveCards();
	}

	public List<Card> getInactiveCards()
	{
		return hand.getInactiveCards();
	}
	/**
	 * The players card list uses 3 delimiters
	 *	The ';' delimits the active list form the inactive list. ActiveCards|InactiveCards
	 *	The ',' delimits the cards in a list from each other. Card1;Card2;Card3
	 *	The ' ' delimits the specifics of a card. CardValue CardCategory
	 * @author Chris
	 * @return
	 */
	public String getCardListForUTF()
	{
		StringBuilder cardList = new StringBuilder();
		
		if(getActiveCards().size()>0)
		{
			for(Card card: getActiveCards())
				cardList.append(card.getVal()+""+card.getSuit()+",");
			cardList.setCharAt(cardList.lastIndexOf(","), ';');
		}
		else
			cardList.append(" ;");
		
		if(getInactiveCards().size()>0)
		{
			for(Card card: getInactiveCards())
				cardList.append(card.getVal()+" "+card.getSuit()+",");
			cardList.deleteCharAt(cardList.lastIndexOf(","));
		}
		else
			cardList.append(' ');
		
		return cardList.toString();
	}
}
