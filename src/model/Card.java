package model;

import java.util.Comparator;

/**
 * @author Antonio Mendiola
 * The model.Card class is meant only to hold the value and suit of a Card.
 * It contains that data, ways to access it, and a method to print its data
 */
public class Card implements Comparator<Card>, Comparable<Card>{
    private Suit suit;
    private Value value;

	public enum Suit {
	    SPADES, HEARTS, CLUBS, DIAMONDS;

        /**
         * @return returns a single character representation of the Suit, with each suit represented by their first character
         */
        public char toChar() {
            return toString().charAt(0);
        }
    }

    public enum Value {
	    ACE, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9, NUM10, JACK, QUEEN, KING;

    	
    	
    	
        /**
         * @return returns a String representation of the Value. Either the number of the value or the first character of the Value
         */
	    public String toChar() {
	        switch(this){
                case ACE:
                    return "A";
                case NUM10:
                    return "T";
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
	    
	    
	    public int toInt()
	    {
	    	switch(this){
            case ACE:
            	return 1;
            case NUM2:
            	return 2;
            case NUM3:
            	return 3;
            case NUM4:
            	return 4;
            case NUM5:
            	return 5;
            case NUM6:
            	return 6;
            case NUM7:
            	return 7;
            case NUM8:
            	return 8;
            case NUM9:
            	return 9;
            case NUM10:
            	return 10;
            case JACK:
            	return 11;
            case QUEEN:
            	return 12;
            case KING:
            	return 13;
            default:
            	return -1;
	    	}
	    }
    }

    /**
     * Constructs a new Card with the provided Suit and Value
     * @param v The Value of the Card
     * @param s The Suit of the Card
     */
	public Card(Value v, Suit s) {
		value = v;
        suit = s;
	}

    /**
     * Constructs a new Card based on its String representation. Invalid Strings throw an exception.
     * @param card String representation of the Card
     * @throws IllegalArgumentException Thrown when String provided does not match a valid representation of a card.
     */
	public Card(String card) throws IllegalArgumentException {
        char v = card.charAt(0);
        switch(v){
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
            case 'T':
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
	    char s = card.charAt(1);
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
        if(!toString().equals(card)) throw new IllegalArgumentException();
	}

    /**
     * @return returns the Card's Value
     */
    public Value getVal() {
		return value;
	}

    /**
     * @return returns the Card's Suit
     */
	public Suit getSuit() {
		return suit;
	}

    /**
     * Checks if 2 cards have the same Value
     * @param o The other Card to be compared with this one
     * @return returns true when both cards have the same Value, false otherwise
     */
	boolean matches(Card o) {
        return value == o.getVal();
    }

	@Override
	public boolean equals(Object card) {
		if(card == this) return true;

		if(card.getClass() != Card.class) return false;

		return ((Card) card).getVal().equals(value)
				&& ((Card) card).getSuit().equals(suit);
	}

	@Override
	public String toString() {
		return value.toChar() + suit.toChar();
	}

	/**
	 * Compares which card is greater than the other or if equal
	 * Utilizes the Value enums built in compareTo() method
	 * @param o1 - First card
	 * @param o2 - Second card
	 * @return 0, -1, or 1
	 */
	@Override
	public int compare(Card o1, Card o2) 
	{
		return o1.getVal().compareTo(o2.getVal());
	}

	@Override
	public int compareTo(Card o) {
		
		return compare(this,o);
	}
}