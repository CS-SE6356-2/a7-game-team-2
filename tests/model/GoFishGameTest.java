package model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class GoFishGameTest {

	public static void main(String[] args) {

		ArrayList<String> names = new ArrayList<>();
		names.add("test1");
		names.add("test2");
		
		ArrayList<Socket> socks = new ArrayList<>();
		socks.add(new Socket());
		socks.add(new Socket());

		GoFishGame testGame = new GoFishGame(names, socks, new File("resources\\cardlist.txt"));
		
		testGame.shuffleCards();
		testGame.dealCards();
		
		System.out.println(testGame.cardDeck.getNumOfCards());
		System.out.println(testGame.getPlayerList().get(0).getCardListForUTF());
		System.out.println(testGame.getPlayerList().get(0).getCardListForUTF());
		
		PlayerQueue.PlayerQueueIterator iter = testGame.getPlayerQueue().new PlayerQueueIterator(testGame.getPlayerQueue());
		while(iter.hasNext()) {
			System.out.println("Player " + iter.next().getName());
		}
		
		int playerTurn = 0;
		
		// test 20 turns with each player choosing a random value from the cards in their deck
		for(int i = 0; i < 100; i++) {
			System.out.println("\n\nTurn " + i + ":");
			if(testGame.getPlayerList().get(playerTurn).getActiveCards().isEmpty()) {
				if(testGame.cardDeck.isEmpty()) {
					testGame.getPlayerList().get(1 - playerTurn).checkBooks();
					testGame.lostCards(testGame.getPlayerList().get(playerTurn));
					break;
				}
				else {
					testGame.getPlayerList().get(playerTurn).addCard(testGame.cardDeck.takeCard());
					playerTurn = 1 - playerTurn;
					continue;
				}
				
			}
			int randomCard = new Random().nextInt(testGame.getPlayerList().get(playerTurn).getActiveCards().size());
			Card.Value randomValue = testGame.getPlayerList().get(playerTurn).getActiveCards().get(randomCard).getVal();
			
			boolean containsValue = testGame.queryPlayer(randomValue, testGame.getPlayerList().get(playerTurn), testGame.getPlayerList().get(1 - playerTurn));
			System.out.println("player " + (playerTurn + 1) + " testing " + randomValue);
			System.out.println("player " + (playerTurn + 1) + " goes again: " + containsValue);

			System.out.println(testGame.getPlayerList().get(0).getCardListForUTF());
			System.out.println(testGame.getPlayerList().get(1).getCardListForUTF());
			
			if(!containsValue) playerTurn = 1 - playerTurn;
		}
		
		iter = testGame.getPlayerQueue().new PlayerQueueIterator(testGame.getPlayerQueue());
		while(iter.hasNext()) {
			System.out.println("Player " + iter.next().getName());
		}
		System.out.println(testGame.cardDeck.getNumOfCards());

	}

}
