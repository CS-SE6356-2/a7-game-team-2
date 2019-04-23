package model;/* @author Jacob  */

import java.util.LinkedList;
import java.util.List;


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

		for (int card1Index = 0;
		     card1Index < activeCards.size(); 
		     ++card1Index)
		{
			for (int card2Index = card1Index + 1;
			     card2Index < activeCards.size();
			     ++card2Index)
			{
				Card card1 = activeCards.get(card1Index);
				Card card2 = activeCards.get(card2Index);

				if (false /*TODO card1.matches(card2)*/)
				{
					matchingCards.add(card1);
					matchingCards.add(card2);
				}
			}
		}
		return matchingCards;
	}

	/* Adds all the cards in the list to the active cards */
	public void addCards(LinkedList<Card> cards)
	{
		activeCards.addAll(cards);
	}

	/* Removes all the cards in the list from the active cards,
	 * returning a list of all cards successfully removed */
	public LinkedList<Card> removeCards(LinkedList<Card> cards)
	{
		LinkedList<Card> removedCards = new LinkedList<>();
		for (int index = 0;
		     index < cards.size();
		     ++index)
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
	public LinkedList<Card> transferActiveToInactive(LinkedList<Card> cards)
	{
		LinkedList<Card> transferredCards = new LinkedList<Card>();
		for (int index = 0;
		     index < cards.size();
		     ++index)
		{
			Card cardToTransfer = cards.get(index);
			if (activeCards.remove(cardToTransfer))
			{
				inactiveCards.add(cardToTransfer);
				transferredCards.add(cardToTransfer);
			}
		}
		return transferredCards;
	}

	/* Transfers all the cards in the list from inactive cards to active cards
	 * and returns a list of all cards successfully transferred */
	public LinkedList<Card> transferInactiveToActive(LinkedList<Card> cards)
	{
		LinkedList<Card> transferredCards = new LinkedList<Card>();
		for (int index = 0;
		     index < cards.size();
		     ++index)
		{
			Card cardToTransfer = cards.get(index);
			if (inactiveCards.remove(cardToTransfer))
			{
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
	public void setInactiveCards(List<Card> inactiveCards)
	{
		inactiveCards = new LinkedList<>(inactiveCards);
	}

	public int getNumOfCards() {
		return activeCards.size();
	}
}
