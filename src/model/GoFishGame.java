package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

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
	 *  Checks if source has card; if true, transfer all of that type of card from source to target, otherwise target draws one card
	 *  @param value - String with suit of card target is testing source with
	 *  @param target - Player querying source
	 *  @param source - Player being query'd by target
	 *  @return true if source has card type, false if not
	 */
	public boolean queryPlayer(String value, Player target, Player source) {
		if(source.hasCardType(value)) {
			transferCardsFromOther(value, target, source);
			return true;
		}else {
			target.addCards((LinkedList<Card>) pile.takeCards(1)); // target draws one card if source does not contain any cards of category type
			return false;
		}
	}
	
	/**
	 *  Move all cards with value [value] from source to target
	 *  @param value - String with suit of card target is testing source with
	 *  @param target - Player querying source
	 *  @param source - Player being query'd by target
	 */
	public void transferCardsFromOther(String value, Player target, Player source) {
		target.addCards((LinkedList<Card>) source.shedCards(value));
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