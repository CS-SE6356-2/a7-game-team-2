package model;

import java.util.Iterator;

import model.PlayerQueue.Node;
import model.PlayerQueue.PlayerQueueIterator;

public class GoFishQueue extends PlayerQueue
{
	/**
	 * Removes the listed player from the queue
	 * @param person
	 * @return
	 */
	public Player dequeue(Player person) 
	{
		if(size == 0) //If the queue is empty
			return null;
		else if(size == 1)	//If the queue has only a head
			head = null;
		else //if size > 1
		{
			if(head.data == person)	//If the head references the same person we are removing
			{
				head.prev.next = head.next;
				head.next.prev = head.prev;
				head = head.next;
			}
			else //the person is contained somewhere else in the list
			{
				Node focus = head.prev;
				int i;
				for(i = 0; i < size; ++i)
				{
					if(focus.data == person)
						break;
					focus = focus.prev;
				}
				
				if(i == size)	//If we exited the loop by size
					return null;	//Return null as we did not find the player
				
				focus.prev.next = focus.next;
				focus.next.prev = focus.prev;
			}
		}
		--size;
		return person; // return the Player that we found
	}
	
	@Override
	public Iterator<Player> iterator() 
	{	
		return new GoFishQueueIterator(this);
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