package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Class representing the central Deck of Cards, which begins at full and distributes Cards through the takeCard function.
 * @author Tyler Heard & Antonio Mendiola
 */
public class Deck {
	private List<Card> cards;

	/**
	 * Creates a Deck and instantiates it with a full 52 card deck
	 */
	Deck() {
		cards = new LinkedList<>();
		for (Card.Suit s: Card.Suit.values()) {
			for (Card.Value v: Card.Value.values()) {
				cards.add(new Card(s, v));
			}
		}
	}

	/**
	 * Creates a Deck and instantiates it with all of the cards listen in the cardList file
	 * @param cardList File containing list of cards to fill Deck with
	 */
	Deck(File cardList)
	{
		cards = new LinkedList<>();
		Scanner input = new Scanner(System.in);
		try {
			input = new Scanner(cardList);
		}
		catch(FileNotFoundException e) {
			System.out.println("cardList file not found!");
			System.exit(1);
		}

		while(input.hasNextLine())
		{
			String card = input.nextLine();
			cards.add(new Card(card));
		}
	}

	/**
	 * Shuffles the deck using Collections.shuffle
	 */
	void shuffle()
	{
		Collections.shuffle(cards);
		/*
		//Making a Random object to run Fisher-Yates shuffle
		Random rand = new Random();
		
		//For loop to run over each item in the array
		for(int i = getNumOfCards()-1; i > 0; i --)
		{
			//Getting a random index 0 <= j < i
			int j = rand.nextInt(i+1);
			Card temp = cards.get(i);
			cards.set(i, cards.get(j));
			cards.set(j, temp);
		}
		*/
	}

	List<Card> getCards(){
		return cards;
	}

	/**
	 * Removes a single card from the top of the Deck
	 * @return Returns the removed Card
	 */
	Card takeCard(){
		if(cards.isEmpty()) return null;
		return cards.remove(0);
	}
	
	/**
	 * Removes up to the given amount of cards from the deck.
	 * To be used for refilling the player's hand.
	 * returns an empty list if there are no more cards
	 * @param startingHandSize - Either a 5 or 7 depending on the amount of players that started the game
	 * @return A list of cards that were removed from the deck or an empty list
	 */
	List<Card> refillHand(int startingHandSize)
	{
		List<Card> newHand = new ArrayList<>();
		for(int i = 0; !isEmpty() && i < startingHandSize; ++i)
			newHand.add(takeCard());
		return newHand;
	}

	/**
	 * Finds the ith card in the Deck, from the top, and returns it, leaving the card in the Deck
	 * @param i The position of the card to find
	 * @return Returns the card found at the ith position
	 */
	Card getCardAt(int i) throws IndexOutOfBoundsException{
		return cards.get(i);
	}

	/**
	 * @return Returns the number of Cards left in the Deck
	 */
	int getNumOfCards(){
		return cards.size();
	}

	/**
	 * Returns whether or not the Deck is empty
	 * @return Returns true if the Deck is empty, false otherwise
	 */
	boolean isEmpty(){
		return cards.isEmpty();
	}

	@Override
	public boolean equals(Object o){
		if(o == this) return true;

		if(o.getClass() != Deck.class) return false;

		return ((Deck) o).getCards().equals(cards);
	}
}