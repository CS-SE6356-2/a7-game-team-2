package model;

public class GoFishQueue extends PlayerQueue
{
	
	public Player playAgain() {return getPlayer();}
	
	public Player dequeue(Player person) {
		return person;
	}
	
	private void dequeue(Node n) {
		
	}
}