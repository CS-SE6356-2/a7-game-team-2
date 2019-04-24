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
	 * Finds all the pairs of cards which share the same value within the Hand's activeCards
	 * @return List of matched cards in the Hand
	 */
    List<Card> checkMatches()
	{
		List<Card> matchingCards = new LinkedList<>();

		for (int card1Index = 0; card1Index < activeCards.size(); ++card1Index)
		{
            Card card1 = activeCards.get(card1Index);
            if (matchingCards.contains(card1)) continue;
			for (int card2Index = card1Index + 1; card2Index < activeCards.size(); ++card2Index)
			{
				Card card2 = activeCards.get(card2Index);

				if (card1.matches(card2))
				{
					matchingCards.add(card1);
					matchingCards.add(card2);
					break;
				}
			}
		}
		return matchingCards;
	}

	/**
	 * Returns a list of cards of the pairs that are held by a player
	 * Each card is separated by spaces
	 * Card suit and card value are stuck together
	 * IE
	 * @return		DA S2 A3 D8 DT DK
	 * @author Chris
	 */
	public String findMatches() 
	{
		TreeSet<Card> uniqueCards = new TreeSet<Card>();
		StringBuilder cardList = new StringBuilder();

		for(Card card: inactiveCards)
			uniqueCards.add(card);

		for(Card uCard: uniqueCards)
			cardList.append(uCard.getSuit().toChar()+""+uCard.getVal().toChar()+" ");
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
    public int getNumCards()
    {
        return activeCards.size() + inactiveCards.size();
    }

}
