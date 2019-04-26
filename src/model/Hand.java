package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class representing a Player's Hand, which contains both the cards they can play, and the inactive cards which belong to them.
 * @author Jacob & Antonio Mendiola
 */
public class Hand
{
	// All the cards that are able to be played
	private List<Card> activeCards;

	// Cards the hand owns but cannot use (e.g. matched cards)
	private List<Card> inactiveCards;

	public Hand()
	{
		activeCards = new LinkedList<>();
		inactiveCards = new LinkedList<>();
	}

	/**
	 * Finds all the Cards whose Values are equal to the provided Value within the Hand's activeCards
     * @param value Value to find matches with
	 * @return List of matched cards in the activeCards list
	 */
    List<Card> checkMatches(Card.Value value)
	{
		return activeCards.stream().filter(card -> card.getVal().equals(value)).collect(Collectors.toList());
	}

	/**
	 * Returns a list of cards of the pairs that are held by a player
	 * @return		DA S2 A3 D8 DT DK
	 * @author Chris
	 */
	public String findMatches() 
	{
        StringBuilder cardList = new StringBuilder();
        TreeSet<Card> uniqueCards = new TreeSet<>(inactiveCards);

		for(Card uCard: uniqueCards)
			cardList.append(uCard.toString()+" ");
		cardList.deleteCharAt(cardList.lastIndexOf(" "));

		return cardList.toString();
	}

    /**
     * Adds a card to the activeCards list
     * @param card Card to add to the activeCards list
     */
	void addCard(Card card){
	    activeCards.add(card);
    }

	/**
	 * Adds all cards in the provided list to the activeCards list
	 * @param cards List of Cards to add
	 */
    void addCards(List<Card> cards)
	{
		activeCards.addAll(cards);
	}

	/**
	 * Removes every card in the provided list from the activeCards list and returns the list of Cards removed.
	 * @param cards List of Cards to be removed from the activeCards list
	 * @return Returns the list of removed cards
	 */
    List<Card> removeCards(List<Card> cards)
	{
	    Map<Boolean, List<Card>> partitioned = activeCards.stream().collect(Collectors.partitioningBy(cards::contains));
	    activeCards = partitioned.get(false);
	    return partitioned.get(true);
	}

    /**
     * Transfers all Cards from cards list found in activeCards list to the inactiveCards list
     * @param cards List of Cards to transfer
     * @return Returns list of transferred Cards
     */
    List<Card> transferActiveToInactive(List<Card> cards)
	{
	    List<Card> ret = removeCards(cards);
	    inactiveCards.addAll(ret);
	    return ret;
	}

    /**
     * @return Returns the activeCards list
     */
    List<Card> getActiveCards()
	{
		return activeCards;
	}

    /**
     * @return Returns the inactiveCards list
     */
	List<Card> getInactiveCards()
	{
		return inactiveCards;
	}

    /**
     * Sets the activeCards list to the provided list of Cards
     * @param cards New list of active Cards
     */
	public void setActiveCards(List<Card> cards)
	{
		activeCards = new LinkedList<>(cards);
	}

    /**
     * @return Returns the number of active Cards in the Hand
     */
	int getNumActiveCards()
	{
		return activeCards.size();
	}

    /**
     * @return Returns the number of inactive Cards in the Hand
     */
    int getNumInactiveCards()
    {
        return inactiveCards.size();
    }

    /**
     * @return Returns the total number of Cards in the Hand, both active and inactive
     */
    int getNumCards()
    {
        return activeCards.size() + inactiveCards.size();
    }

}
