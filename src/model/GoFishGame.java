package model;

import java.io.File;
import java.net.Socket;
import java.util.*;

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
	 *  @param source - Player querying target
	 *  @param target - Player being query'd by source
	 *  @return true if target has card type, false if not
	 */
	public boolean queryPlayer(Card.Value value, Player source, Player target) {
		List<Card> query = target.getCardsOfValue(value);
		if(!query.isEmpty()) {
			transferCardsFromOther(query, source, target);
			return true;
		}else {
			source.addCard(cardDeck.takeCard()); // source draws one card if target does not contain any cards of category type
			return false;
		}
	}
	
	/**
	 *  Move all cards with value 'value' from target to source
	 *  @param cards - String with suit of card source is testing target with
	 *  @param source - Player querying target
	 *  @param target - Player being query'd by source
	 */
	public void transferCardsFromOther(List<Card> cards, Player source, Player target) {
		source.addCards(target.removeCards(cards));
	}
	
	public String determineWinner(PlayerQueue playerList) {
		Iterator<Player> playerIter = playerList.iterator();
		// TODO
		return "";
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