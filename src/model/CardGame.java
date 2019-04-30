package model;

import java.io.File;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CardGame
{
	List<Player> players;
	Deck cardDeck;
	PlayerQueue queue;

	public CardGame(List<String> playerNames, List<Socket> clientSocks)
	{
		players = IntStream.range(0, playerNames.size()).mapToObj(i -> new Player(playerNames.get(i), "Solo", clientSocks.get(i))).collect(Collectors.toList());
		cardDeck = new Deck();
		queue = new PlayerQueue();
	}

	public CardGame(List<String> playerNames, List<Socket> clientSocks, File cardList)
	{
		players = IntStream.range(0, playerNames.size()).mapToObj(i -> new Player(playerNames.get(i), "Solo", clientSocks.get(i))).collect(Collectors.toList());
		cardDeck = new Deck(cardList);
		queue = new PlayerQueue();
	}

	public Player getPlayer(String playerName){
		return players.stream().filter(player -> player.getName().equals(playerName)).findFirst().orElse(null);
	}

	public void shuffleCards(){
		cardDeck.shuffle();
	}
	
	/**
	 * 
	 */
	public void dealCards()
	{
		for(int i = 0; i < getStartingHandSize(); i++){
			for (Player player : players) {
				player.addCard(cardDeck.takeCard());
			}
		}
	}

	int getStartingHandSize() {
		if(players.size() < 4) return 7;
		else return 5;
	}

	/**
	 * Sorts the list of players initially in a game by finding the dealer, adding them and the other players into a circular linked list called playerQueue
	 * @author Chris
	 * @return playerQueue
	 */
	public PlayerQueue sortPlayersInPlayOrder() {
		for(Player player : players)
			queue.enqueue(player);
		queue.poll();
		//CLIENTSOCKS AND CLIENTLABELS are automatically sorted within the playerQueue as they are part of the model.Player object
		
		int dealerNum;	//Track the index of the dealer
		 //Index through array until dealer is found, if not then stop at end of list
		//for(dealerNum = 0;dealerNum < players.size() && !players.get(dealerNum).getRole().equals("Dealer"); dealerNum++);
		
		return queue;
	}

	public Player getNextPlayer() {
		return queue.poll();
	}
	/**
	 * Assigns the given player as the new dealer.
	 * @author Chris
	 * @param newDealer
	 * @return True if a new dealer has been assigned | False if not
	 */
	public boolean assignDealer(String newDealer)
	{
		for(Player p: players)
			if(p.getName().equals(newDealer))
			{
				p.assignRole("Dealer");
				return true;
			}
		return false;
	}
	
	private int getMatchValue(List<Card> match)
	{
		//TODO
		return 21;
	}

	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO Extend Game Logic here,
		//      i.e. what kind of card
		//      or action did they make
		return true;
	}
	
	/**
	 * Checks to see if a player has met the winning conditions
	 * @author Chris
	 * @return
	 */
	public boolean checkWinCondition(Player focusPlayer, String move)
	{
		//TODO extend into a specific game type (set of rules)
		return focusPlayer.getNumOfCards() == 0;
	}
	
	/**
	 * 
	 */
	public boolean queryPlayer(String sourceName, String cardValue, String targetName) {
		return false;
	}

}