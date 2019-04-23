package model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.net.Socket;

import java.util.Iterator;

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
	
	public boolean queryPlayer(Card card, Player source, Player target) {
		// TODO
		return false;
	}
	
	public String determineWinner(PlayerQueue playerList) {
		Iterator<Player> playerIter = playerList.iterator();
		// TODO
		return null;
	}
}