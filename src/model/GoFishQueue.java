package model;

import java.util.Iterator;

public class GoFishQueue extends PlayerQueue
{
	
	public Player playAgain() {return getPlayer();}

	
	public Player dequeue(Player person) {
		return person;
	}
	
	private void dequeue(Node n) {
		
	}
	
	class GoFishQueueIterator implements Iterator<Player>
	{
		Node focus;
		int counter;

		public GoFishQueueIterator(GoFishQueue playerQueue) 
		{
			focus = playerQueue.getHead();
			counter = 0;
		}

		@Override
		public boolean hasNext() 
		{
			return counter < getSize();
		}

		@Override
		public Player next() 
		{
			++counter;
			Player temp = focus.data;
			focus = focus.next;
			return temp;
		}
		
	}

}