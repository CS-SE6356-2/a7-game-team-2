package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoFishGame extends CardGame {

	private GoFishQueue playerList;

	public GoFishGame(int numOfPlayers, ArrayList<String> playerNames, ArrayList<Socket> clientSocks, File cardList) {
		super(numOfPlayers, playerNames, clientSocks, cardList);

		playerList = sortPlayersInPlayOrder();
	}

	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO
		return true;
	}

	/**
	 * Checks if target has card; if true, transfer all of that type of card from
	 * target to source, otherwise source draws one card
	 * 
	 * @param value
	 *            - Value of card source is testing target with
	 * @param target
	 *            - Player asking for cards
	 * @param source
	 *            - Player being asked
	 * @return true if target has card type, false if not
	 */
	public boolean queryPlayer(Card.Value value, Player target, Player source) {
		List<Card> query = source.getCardsOfValue(value);
		if (!query.isEmpty()) {
			transferCardsFromOther(query, target, source);
			source.checkPairs();
			checkRemovePlayer(source);
			return true;
		} else {
			if(!cardDeck.getCards().isEmpty())
				target.addCard(cardDeck.takeCard()); // target draws one card if source does not contain any cards of
													// category type
			source.checkPairs();
			return false;
		}
	}

	/**
	 * Move all cards with value [value] from source to target
	 * 
	 * @param cards
	 *            - List of cards to transfer
	 * @param target
	 *            - Player asking for cards
	 * @param source
	 *            - Player being asked
	 */
	public void transferCardsFromOther(List<Card> cards, Player target, Player source) {
		target.addCards(source.removeCards(cards));
	}

	public void checkRemovePlayer(Player source) {
		if (source.getActiveCards().size() == 0 && cardDeck.isEmpty()) {
			getPlayerList().dequeue(source);
		}
	}

	public String determineWinner(PlayerQueue playerList) {
		Iterator<Player> playerIter = playerList.iterator();
		// TODO
		return "";
	}

	public GoFishQueue sortPlayersInPlayOrder() {
		// CLIENTSOCKS AND CLIENTLABELS are automatically sorted within the playerQueue
		// as they are part of the model.Player object

		int dealerNum; // Track the index of the dealer
		// Index through array until dealer is found, if not then stop at end of list
		for (dealerNum = 0; dealerNum < players.length && !players[dealerNum].getRole().equals("Dealer"); dealerNum++)
			;

		// Move number to next in list as dealer doesn't usually go first
		dealerNum = (dealerNum) % players.length;
		// Create the playerQueue
		GoFishQueue playOrder = new GoFishQueue();

		for (int i = 0; i < players.length; i++) // For each player
			playOrder.enqueue(players[(dealerNum + i) % players.length]); // Starting at the dealer, add them to the
																			// queue

		return playOrder; // Return the queue
	}

	/**
	 * @author Chris
	 * Returns the number of cards in each player's hand
	 * It returns a comma separated list of integers where each one left to right 
	 * represents the cards in that respective player's hand.
	 * IE
	 * @return	numOfCardsForPlayer1,numOfCardsForPlayer2,numOfCardsForPlayer3,...
	 */
	public String getAmtCardsPerAHand() 
	{
		StringBuilder cardsPerHand = new StringBuilder();
		for(Player p: players)
			cardsPerHand.append(p.getNumOfCards()+",");
		cardsPerHand.setCharAt(cardsPerHand.lastIndexOf(","), ';');
		return cardsPerHand.toString();
	}

	/**
	 * @author Chris
	 * Returns the pairs in each player's hand
	 * It returns a comma separated list of character sequences where each one left to right 
	 * represents the pairs with that respective player.
	 * The characters for each player are separated by spaces
	 * A player
	 * IE
	 * @return	DA S2 A3 D8 DT DK,A4 D5,0,...
	 */
	public String getPairsPerHand() 
	{
		StringBuilder pairsPerHand = new StringBuilder();
		for(Player p: players)
			pairsPerHand.append(p.getPairs()+",");
		pairsPerHand.setCharAt(pairsPerHand.lastIndexOf(","), ';');
		return " ";
	}

	/**
	 * @author Chris
	 * Returns the number of cards left in the deck
	 * @return
	 */
	public int getAmtCardInDrawDeck() 
	{
		return cardDeck.getNumOfCards();
	}

	public GoFishQueue getPlayerList() {
		return playerList;
	}
}