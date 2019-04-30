package model;

import java.util.Iterator;

/**
 * Provides a means for circling through a list of players, handling skips, and reversing order
 * @author Chris
 */
public class PlayerQueue implements Iterable<Player>
{
	private Node head;			//Tracks the player to go next
	private Node tail;
	protected int size;			//Tracks the amount of players in the queue
	private boolean reversed;	//Tracks when we go to the next player or previous player
	
	/**
	 * Sets variables to null or 0
	 * @author Chris
	 */
	PlayerQueue()
	{
		head = null;
		tail = null;
		size = 0;
		reversed = false;
	}
	
	/**
	 * Adds a new player to the queue
	 * @param person model.Player to be added
	 */
	void enqueue(Player person)
	{
		enqueue(new Node(person));
		++size;
	}
	
	/**
	 * Adds a new Node as either the head or the new tail
	 * @author Chris
	 * @param newNode Node to add to Queue
	 */
	private void enqueue(Node newNode) {
		if(size == 0) {
			head = newNode;
			tail = newNode;
			head.next = head;
			head.prev = head;
		}
		else {
			tail.next = newNode;
			newNode.prev = tail;
			tail = newNode;
			tail.next = head;
			head.prev = tail;
		}
	}
	/**
	 * Moves head to the next player in the queue and returns the old head as
	 * the player that is to take their turn
	 * @author Chris
	 * @return The next model.Player to take their turn
	 */
	Player poll() {
		Player nextPlayer = head.data;
		head = reversed ? head.prev : head.next;
		tail = reversed ? tail.prev : tail.next;
		return nextPlayer;
	}
	
	
	
	/**
	 * Returns the model.Player at the head of the queue
	 * @author Chris
	 * @return A player
	 */
	public Player getPlayer() {
		return head.data;
	}
	/**
	 * Removes the listed player from the queue
	 * @param person Player to remove from the Queue
	 * @return Returns true if person was found and removed, false otherwise
	 */
	boolean remove(Player person) {
		if(size == 0)
			return false;
		Node focus = head;
		int i;
		for(i = 0; i < size; ++i)
		{
			if(focus.data == person)
				break;
			focus = focus.prev;
		}

		if(i == size)
			return false;
		focus.prev.next = focus.next;
		focus.next.prev = focus.prev;
		if(focus == head) head = focus.next;
		if(focus ==tail) tail = focus.prev;
		--size;
		return true;
	}
	/**
	 * Returns the amount of players in the queue
	 * @return The number of players in the queue
	 */
	public int size() {
		return size;
	}
	/**
	 * Moves head to the next player without getting back a player, essentially 
	 * skipping the skipped player's turn
	 * @author Chris
	 */
	void skipPlayer() {
		head = reversed ? head.prev : head.next;
		tail = reversed ? tail.prev : tail.next;
	}
	/**
	 * Sets the reverse flag to its opposite. Calls skipPlayer() twice to set the order to the proper model.Player
	 * @author Chris
	 */
	void reverseOrder()
	{
		reversed = !reversed;
		tail = reversed ? head.next : head.prev;
		skipPlayer();
		skipPlayer();
	}
	/**
	 * Clears the queue
	 * @author Chris
	 */
	public void clear() {
		head = null;
		size = 0;
	}
	
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
	public Iterator<Player> iterator() {
		return new PlayerQueueIterator(this);
	}
	
	Node getHead() {
		return head;
	}

	Node getTail() {
		return tail;
	}
	
	void setHead(Node newHead) {
		head = newHead;
	}

	int getSize()
	{
		return size;
	}

	class PlayerQueueIterator implements Iterator<Player>
	{
		Node focus;
		int counter;

		PlayerQueueIterator(PlayerQueue playerQueue)
		{
			focus = playerQueue.getHead();
			counter = 0;
		}

		@Override
		public boolean hasNext() {
			return counter<getSize();
		}

		@Override
		public Player next() {
			++counter;
			Player temp = focus.data;
			focus = focus.next;
			return temp;
		}
		
	}
}
