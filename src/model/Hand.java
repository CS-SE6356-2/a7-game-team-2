package model;
/* @author Jacob  */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;


/* Represents the cards in a specific model.Player's possession. */
public class Hand
{
/* Data */
	/* All the cards that are able to be played */
	private List<Card> activeCards;

	/* Cards the hand owns but cannot use (e.g. matched cards) */
	private List<Card> inactiveCards;

	public Hand()
	{
		activeCards = new LinkedList<>();
		inactiveCards = new LinkedList<>();
	}

	/* Looks at the activeCards for matches and returns all unique pairs
	 * of matching cards. Games requiring a more sophisticated 
	 * matching function would need to override this function */
	public List<Card> checkMatches()
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

	void addCard(Card card){
	    activeCards.add(card);
    }

	/* Adds all the cards in the list to the active cards */
    void addCards(List<Card> cards)
	{
		activeCards.addAll(cards);
	}

	/* Removes all the cards in the list from the active cards,
	 * returning a list of all cards successfully removed */
    List<Card> removeCards(List<Card> cards)
	{
		List<Card> removedCards = new LinkedList<>();
		for (int index = 0; index < cards.size(); ++index)
		{
			Card cardToRemove = cards.get(index);
			if (activeCards.remove(cardToRemove))
			{
				removedCards.add(cardToRemove);
			}
		}
		return removedCards;
	}

	/* Transfers all the cards in the list from active cards to inactive cards 
	 * and returns a list of all cards successfully transferred */
    List<Card> transferActiveToInactive(List<Card> cards)
	{
		List<Card> transferredCards = new LinkedList<>();
        for (Card cardToTransfer : cards) {
            if (activeCards.remove(cardToTransfer)) {
                inactiveCards.add(cardToTransfer);
                transferredCards.add(cardToTransfer);
            }
        }
		return transferredCards;
	}

	/* Transfers all the cards in the list from inactive cards to active cards
	 * and returns a list of all cards successfully transferred */
    List<Card> transferInactiveToActive(List<Card> cards)
	{
		List<Card> transferredCards = new LinkedList<>();
        for (Card cardToTransfer : cards) {
            if (inactiveCards.remove(cardToTransfer)) {
                activeCards.add(cardToTransfer);
                transferredCards.add(cardToTransfer);
            }
        }
		return transferredCards;
	}

/* Getters */
	public List<Card> getActiveCards()
	{
		return activeCards;
	}
	
	public List<Card> getInactiveCards()
	{
		return inactiveCards;
	}
	
	//These both are used for the hand used in the view.ClientGUI
	//Both make shallow copies of the lists
	public void setActiveCards(List<Card> activeCards)
	{
		activeCards = new LinkedList<>(activeCards);
	}

	public int getNumActiveCards()
	{
		return activeCards.size();
	}

    public int getNumInactiveCards()
    {
        return inactiveCards.size();
    }

    public int getNumCards()
    {
        return activeCards.size() + inactiveCards.size();
    }

}
