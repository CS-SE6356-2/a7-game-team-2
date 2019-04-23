package model;

import java.util.Iterator;

public class GoFishGame extends CardGame
{
	public boolean isLegalMove(Player focusPlayer, String move) {
		// TODO
	}
	
	public boolean queryPlayer(Card card, Player source, Player target) {
		// TODO
	}
	
	public String determineWinner(PlayerQueue playerList) {
		Iterator<Player> playerIter = playerList.iterator();
		// TODO
	}
}