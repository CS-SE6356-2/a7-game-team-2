package model;
/* @author Jacob */

import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import model.Card.Value;

/* Represents one of the people playing the game */
public class Player
{
	/* Name of the team that the player belongs to */
	private String teamName;

	/* Identifier marking the role the player has in the game */
	private String role;

	/* Cards the player possesses (both active and inactive) */
	private Hand hand;
	
	//Socket that the player is connected on
	private Socket playerSock;

	/**
	 * Creates a Player assigned to the given Team, with the given role, and connected with the given Socket.
	 * @param cTeamName The Team name that this Player belongs to
	 * @param cRole String describing this Player's role
	 * @param cSock Socket with which the Player is connected to the Game
	 */
	public Player(String cTeamName, String cRole, Socket cSock)
	{
		teamName = cTeamName;
		role = cRole;
		hand = new Hand();
		playerSock = cSock;
	}

	/**
	 * Adds a single Card to the Player's Hand's activeCards
	 * @param card Card to add to the Hand
	 */
	public void addCard(Card card){
	    hand.addCard(card);
    }

	/**
	 * Adds a list of Cards to the Player's Hand's activeCards
	 * @param cards List of Cards to add to the Hand
	 */
	public void addCards(List<Card> cards)
	{
		hand.addCards(cards);
	}

	/**
	 * Removes all cards found in the provided list from the Player's Hand's activeCards
	 * @param cards List of Cards to remove
	 * @return Returns list of successfully removed Cards
	 */
	public List<Card> removeCards(List<Card> cards)
	{
		return hand.removeCards(cards);
	}
	
	/**
	 * Removes all cards with same value as 'value' and return a list with them
	 * @return List<Card> - list of cards with same value from hand
	 */
	public List<Card> shedMatches(Card.Value value) {
		return hand.removeCards(hand.checkMatches(value));
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
	 * Returns a list of values of the pairs that are held by a player (inactive hand)
	 * Each card value is separated by spaces
	 * Returns a null if the player has no pairs
	 * IE
	 * @param A list of cards to check
	 * @return		DA S2 A3 D8 DT DK or " "
	 * @author Chris
	 */
	public String getPairs(List<Card> cards)
	{
		if(cards.size() == 0)
			return " ";
		else
		{
			return hand.findMatchesForUTF(cards);
		}
	}

	/**
	 * Assigns the Player the given role
	 * @param newRole Role to assign
	 */
	public void assignRole(String newRole){
		role = newRole;
	}

	/**
	 * @return Returns the Player's assigned role
	 */
	public String getRole(){
		return role;
	}

	/**
	 * @return Returns the Player's assigned Team name
	 */
	public String getTeamName(){
		return teamName;
	}

	/**
	 * @return Returns the Socket which belongs to the Player
	 */
	public Socket getSock(){
		return playerSock;
	}
	
	/**
	 * Checks to see if Player has any Cards of the given Suit in their Hand's activeCards
	 * @param suit Suit of Card to check for
	 * @return List of Cards of the given Suit currently in the Player's Hand's activeCards
	 */
	public List<Card> getCardsOfSuit(Card.Suit suit) {
		return hand.getActiveCards().stream().filter(card -> card.getSuit().equals(suit)).collect(Collectors.toList());
	}

	/**
	 * Checks to see if Player has any Cards of given Value in their Hand's activeCards
	 * @param value Value of card to check for
	 * @return List of Cards of the given Value currently in the Player's Hand's activeCards
	 */
	public List<Card> getCardsOfValue(Card.Value value) {
		return hand.getActiveCards().stream().filter(card -> card.getVal().equals(value)).collect(Collectors.toList());
	}

	/**
	 * Transfers all Cards in the given list from the Player's Hand's activeCards to the Hand's inactiveCards
	 * @param cards List of Cards to transfer
	 * @return List of Cards successfully transferred
	 */
	public List<Card> transferActiveToInactive(List<Card> cards)
	{
		return hand.transferActiveToInactive(cards);
	}
	

	/**
	 * Goes through all values in Value and creates a list with the matching cards from hand. The cards are then moved to inactive if there are 4
	 */
	public void checkBooks() {
		for(Value v : Value.values()) {
			List<Card> matches = hand.getActiveCards().stream().filter(card -> card.getVal().equals(v)).collect(Collectors.toList());
			if(matches.size() == 4) {
				transferActiveToInactive(matches);
			}
		}
	}

	/**
	 * @return Returns the Player's Hand's activeCards list
	 */
	public List<Card> getActiveCards() {
		return hand.getActiveCards();
	}

	/**
	 * @return Returns the Player's Hand's inactiveCards list
	 */
	public List<Card> getInactiveCards() {
		return hand.getInactiveCards();
	}
	/**
	 * The players card list uses 3 delimiters
	 *	The ';' delimits the active list form the inactive list. ActiveCards;InactiveCards
	 *	The ' ' delimits the cards in a list from each other. Card1,Card2,Card3
	 * @author Chris
	 * @return Returns String representation of the Player's Hand
	 */
	public String getCardListForUTF()
	{
		StringBuilder cardList = new StringBuilder();
		
		//Get all the active cards
		if(getActiveCards().size()>0)
		{
			for(Card card: getActiveCards())
				cardList.append(card.toString()).append(" ");
			cardList.setCharAt(cardList.lastIndexOf(" "), ';');
		}
		else
			cardList.append(" ;");
		
		//Get the unique inactive cards
		cardList.append(getPairs(getInactiveCards()));
		
		return cardList.toString();
	}
}
