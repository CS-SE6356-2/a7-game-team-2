package model;
/*
	Programmer: Tyler Heald
	Date: 3/30/2019
	Description:
	The model.Card class is meant only to hold the value and suit of a model.Card(number and suite).
	It contains that data, ways to access it, and a method to print its data
	
	METHODS:
	printCard()
		Prints the cards data in the format "suit value"
*/

public class Card{
	//DATA FIELDS
    private Suit suit;
    private Value value;

	public enum Suit{
	    SPADES, HEARTS, CLUBS, DIAMONDS;

        public char toChar() {
            return toString().charAt(0);
        }
    }

    public enum Value{
	    ACE, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9, NUM10, JACK, QUEEN, KING;

	    public String toShortString(){
	        switch(this){
                case ACE:
                    return "A";
                case JACK:
                    return "J";
                case QUEEN:
                    return "Q";
                case KING:
                    return "K";
                default:
                    return toString().substring(3);
            }
        }
    }
	
	/****	CONSTRUCTORS	****/
	public Card(Suit s, Value v)
	{
        suit = s;
		value = v;
	}
	
	public Card(String card) throws IllegalArgumentException {
	    char s = card.charAt(0);
	    switch (s){
            case 'S':
                suit = Suit.SPADES;
                break;
            case 'H':
                suit = Suit.HEARTS;
                break;
            case 'C':
                suit = Suit.CLUBS;
                break;
            case 'D':
                suit = Suit.DIAMONDS;
                break;
            default:
                throw new IllegalArgumentException();
        }
		char val = card.charAt(1);
		switch(val){
            case 'A':
                value = Value.ACE;
                break;
            case '2':
                value = Value.NUM2;
                break;
            case '3':
                value = Value.NUM3;
                break;
            case '4':
                value = Value.NUM4;
                break;
            case '5':
                value = Value.NUM5;
                break;
            case '6':
                value = Value.NUM6;
                break;
            case '7':
                value = Value.NUM7;
                break;
            case '8':
                value = Value.NUM8;
                break;
            case '9':
                value = Value.NUM9;
                break;
            case '1':
                value = Value.NUM10;
                break;
            case 'J':
                value = Value.JACK;
                break;
            case 'Q':
                value = Value.QUEEN;
                break;
            case 'K':
                value = Value.KING;
                break;
            default:
                throw new IllegalArgumentException();
        }
	}

	/****	FUNCTIONS	****/
    Value getVal()
	{
		return value;
	}
	Suit getSuit()
	{
		return suit;
	}

	// Override .equals for easier comparison
	@Override
	public boolean equals(Object card) {
		if(card == this) return true;

		if(card.getClass() != Card.class) return false;

		return ((Card) card).getVal().equals(value)
				&& ((Card) card).getSuit().equals(suit);
	}

	// Override .toString for easier printing
	@Override
	public String toString() {
		return suit.toChar() + value.toShortString();
	}
}