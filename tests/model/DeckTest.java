package model;

import model.Card;
import model.Deck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
	private Deck d1, d2;
	private Card c1, c2, c3;

    @BeforeEach
    void setUp() {
    	d1 = new Deck();
    	d2 = new Deck(new File("resources\\cardlist.txt"));
    	c1 = new Card(Card.Suit.SPADES, Card.Value.ACE);
    	c2 = new Card(Card.Suit.HEARTS, Card.Value.NUM2);
    	c3 = new Card(Card.Suit.DIAMONDS, Card.Value.KING);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shuffle() {
    	Deck d3 = new Deck();
    	assertEquals(d3, d1);
    	d1.shuffle();
    	assertNotEquals(d3, d1);
    }

    @Test
    void takeCard() {
    	int cur = d1.getNumOfCards();
    	assertEquals(c1, d1.takeCard());
    	assertEquals(cur-1, d1.getNumOfCards());
    	for(int i = 1; i < 14; i++) d1.takeCard();
		cur = d1.getNumOfCards();
		assertEquals(c2, d1.takeCard());
		assertEquals(cur-1, d1.getNumOfCards());
		for(int i = 15; i < 51; i++) d1.takeCard();
		cur = d1.getNumOfCards();
		assertEquals(c3, d1.takeCard());
		assertEquals(cur-1, d1.getNumOfCards());
		assertThrows(IndexOutOfBoundsException.class, () -> d1.takeCard());
    }

    @Test
    void getCardAt() {
		assertEquals(c1, d1.getCardAt(0));
		assertEquals(c2, d1.getCardAt(14));
		assertEquals(c3, d1.getCardAt(51));
		assertThrows(IndexOutOfBoundsException.class, () -> d1.getCardAt(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> d1.getCardAt(52));
    }

    @Test
    void getNumOfCards() {
    	for(int i = 0; i < 52; i++){
    		assertEquals(52-i, d1.getNumOfCards());
    		d1.takeCard();
		}
		assertEquals(0, d1.getNumOfCards());
    }

    @Test
    void isEmpty() {
    	for(int i = 0; i < 52; i++){
    		assertFalse(d1.isEmpty());
    		d1.takeCard();
		}
		assertTrue(d1.isEmpty());
    	assertFalse(d2.isEmpty());
    }
}