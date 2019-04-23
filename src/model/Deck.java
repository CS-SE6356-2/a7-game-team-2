package model;/*
	Programmer: Tyler Heald
	Date: 3/30/2019
	Description:
	The model.Deck class contains the cards needed for playing a game.
	When the model.Deck object is initialized with certain parameters from an external
	file, it creates cards with the proper values. The only thing the deck can do
	is return cards, remove cards, and shuffle itself.
	
	File cardList:
	The file that contains cards to make for the deck is in the format:
	#ncards
	value1 category1
	value2 category2
	...
	valuen categoryn
	
	METHODS:
	shuffle()
		Shuffles the deck using Fisher-Yates
	getCardAt(int i)
		Gets the card at a given index
	getNumOfCards()
		Gets the number of cards in the deck
	
	TODO:
	Add customization options to reading the cardList for easier processing.
	Example: C as the header so the program can generate cards with values in
	(values X category). Would make it easier to write a file for decks like the
	typical 52 card deck.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Deck {
	//DATA FIELDS
	List<Card> cards;
	
	/****	CONSTRUCTORS	****/
	public Deck() {
		cards = new LinkedList<>();
		for (Card.Suit s: Card.Suit.values()) {
			for (Card.Value v: Card.Value.values()) {
				cards.add(new Card(s, v));
			}
		}
	}

	public Deck(File cardList)
	{
		cards = new LinkedList<>();
		//Creating a scanner to read in the cardList
		Scanner input = new Scanner(System.in);
		try {
			input = new Scanner(cardList);
		}	catch(FileNotFoundException e) {
			System.out.println("cardList file not found!");
			System.exit(1);
		}

		//Read in all the cards for deckSize
		while(input.hasNextLine())
		{
			String card = input.next();
			cards.add(new Card(card));
		}
	}

	/****	FUNCTIONS	****/
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

	/****	GETTERS/SETTERS	****/
	public Card takeTopCard(){
		return cards.remove(0);
	}

	//getCardAt returns the ith card, starting from 0
	Card getCardAt(int i){
		return cards.get(i);
	}

	public int getNumOfCards(){
		return cards.size();
	}

	public boolean isEmpty(){
		return cards.isEmpty();
	}
}