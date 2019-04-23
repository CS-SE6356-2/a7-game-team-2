package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GoFishGame extends CardGame
{

	public GoFishGame(int numOfPlayers, ArrayList<String> playerNames, ArrayList<Socket> clientSocks,
		File cardList) {
		super(numOfPlayers, playerNames, clientSocks, cardList);
	}
	
	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO
		return true;
	}
	
	/**
	 *  Checks if target has card; if true, transfer all of that type of card from target to source, otherwise source draws one card
	 *  @param value - Value of card source is testing target with
	 *  @param target - Player asking for cards
   *  @param source - Player being asked
	 *  @return true if target has card type, false if not
	 */
	public boolean queryPlayer(Card.Value value, Player target, Player source) {
		List<Card> query = source.getCardsOfValue(value);
		if(!query.isEmpty()) {
			transferCardsFromOther(query, target, source);
			return true;
		}else {
			target.addCard(cardDeck.takeCard()); // taarget draws one card if source does not contain any cards of category type
			return false;
		}
	}
	
	/**
	 *  Move all cards with value [value] from source to target
	 *  @param value - String with suit of card target is testing source with
	 *  @param target - Player asking for cards
   *  @param source - Player being asked
	 */
	public void transferCardsFromOther(List<Card> cards, Player target, Player source) {
		target.addCards(source.removeCards(cards));
	}
	
	public String determineWinner(PlayerQueue playerList) {
		Iterator<Player> playerIter = playerList.iterator();
		// TODO
		return "";
	}
	
	public GoFishQueue sortPlayersInPlayOrder() {
		GoFishQueue playerList = (GoFishQueue) super.sortPlayersInPlayOrder();
		
		return playerList;
	}

	public String getAmtCardsPerAHand() {
		// TODO Auto-generated method stub
		return " ";
	}

	public String getPairsPerHand() {
		// TODO Auto-generated method stub
		return " ";
	}

	public int getAmtCardInDrawPile() {
		// TODO Auto-generated method stub
		return 52;
	}
}