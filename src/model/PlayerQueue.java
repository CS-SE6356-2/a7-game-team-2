package model;

import java.util.Iterator;

/**
 * Provides a means for circling through a list of players, handling skips, and reversing order
 * @author Chris
 */
public class PlayerQueue implements Iterable<Player>
{
	////Member Variables////
	Node head;			//Tracks the player to go next
	int size;			//Tracks the amount of players that are playing
	boolean reversed;	//Tracks when we go to the next player or previous player
	
	////Constructor////
	
	/**
	 * Sets variables to null or 0
	 * @author Chris
	 */
	PlayerQueue()
	{
		head = null;
		size = 0;
		reversed = false;
	}

	////Functions////
	
	/**
	 * Adds a new player to the queue
	 * @param person model.Player to be added
	 * @return The model.Player that was added
	 */
	public Player enqueue(Player person)
	{
		enqueue(new Node(person));
		++size;
		return person;
	}
	
	/**
	 * Adds a new Node as either the head or the new tail
	 * @author Chris
	 * @param newNode
	 */
	private void enqueue(Node newNode)
	{
		if(size == 0)				//Put the player at the head if there are no other players
		{
			head = newNode;
			getHead().next = getHead();
			getHead().prev = getHead();
		}
		else if (size == 1)			//Put the player as the tail. This separates the head from being the tail
		{
			getHead().next = newNode;
			getHead().prev = newNode;
			newNode.next = getHead();
			newNode.prev = getHead();
		}
		else			//Adds the player in the queue
		{
			//Set a pointer to the old tail
			Node tail = getHead().prev;
			//Insert the new node into the list
			tail.next = newNode;
			getHead().prev = newNode;
			//Have the new node point to its respective next and prev
			newNode.next = getHead();
			newNode.prev = tail;
		}
	}
	
	
	
	/**
	 * Returns the model.Player at the head of the queue
	 * @author Chris
	 * @return A player
	 */
	public Player getPlayer() {return getHead().data;}
	/**
	 * Returns the amount of players in the queue
	 * @return The number of players in the queue
	 */
	public int size() {return size;}
	/**
	 * Moves head to the next player in the queue and returns the old head as 
	 * the player that is to take their turn
	 * @author Chris
	 * @return The next model.Player to take their turn
	 */
	public Player nextPlayer()
	{
		head = reversed? getHead().prev: getHead().next;
		return reversed? getHead().next.data: getHead().prev.data;
	}
	/**
	 * Moves head to the next player without getting back a player, essentially 
	 * skipping the skipped player's turn
	 * @author Chris
	 */
	public void skipPlayer()
	{
		head = (reversed)? getHead().prev: getHead().next;
	}
	/**
	 * Sets the reverse flag to its opposite. Calls skipPlayer() twice to set the order to the proper model.Player
	 * @author Chris
	 */
	public void reverseOrder() 
	{
		reversed = !reversed;
		skipPlayer();
		skipPlayer();
	}
	/**
	 * Clears the queue
	 * @author Chris
	 */
	public void clear() {head = null; size = 0;}
	
	/**
	 * A node to hold a model.Player and is able to look backwards and forwards
	 * @author Chris
	 */
	class Node
	{
		Player data;
		Node next;
		Node prev;
		
		Node(Player data)
		{
			this.data = data;
			next = null;
			prev = null;
		}
	}

	@Override
	public Iterator<Player> iterator() 
	{	
		return new PlayerQueueIterator(this);
	}
	
	public Node getHead() 
	{
		return head;
	}
	
	public void setHead(Node newHead) 
	{
		head = newHead;
	}

	public int getSize() 
	{
		return size;
	}

	class PlayerQueueIterator implements Iterator<Player>
	{
		Node focus;
		int counter;

		public PlayerQueueIterator(PlayerQueue playerQueue) 
		{
			focus = playerQueue.getHead();
			counter = 0;
		}

		@Override
		public boolean hasNext() 
		{
			return counter<getSize();
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
