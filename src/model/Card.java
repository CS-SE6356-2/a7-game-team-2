package model;
/*
	Programmer: Tyler Heald
	Date: 3/30/2019
	Description:
	The model.Card class is meant only to hold the value and category of a model.Card(number and suite).
	It contains that data, ways to access it, and a method to print its data
	
	METHODS:
	printCard()
		Prints the cards data in the format "value category"
*/

public class Card{
	//DATA FIELDS
	String value;
	String category;
	
	/****	CONSTRUCTORS	****/
	public Card(String v, String c)
	{
		value = v;
		category = c;
	}
	
	public Card(String card) 
	{
		int delimiter = card.indexOf(" ");
		value = card.substring(0, delimiter);
		category = card.substring(delimiter, card.length());
	}

	/****	FUNCTIONS	****/
	//Method to print card stats
	public void printCard()
	{
		System.out.println(value + " " + category);
	}
	/****	GETTERS/SETTERS	****/
	void setVal(String v)
	{
		value = v;
	}
	String getVal()
	{
		return value;
	}
	void setCategory(String c)
	{
		category = c;
	}
	String getCategory()
	{
		return category;
	}
	
	// Override .equals for easier comparison
	@Override
	public boolean equals(Object card) {
		if(card == this) return true;
		
		if(card.getClass() != Card.class) return false;
		
		return ((Card) card).getVal() == this.getVal()
				&& ((Card) card).getCategory() == this.getCategory();
	}
}