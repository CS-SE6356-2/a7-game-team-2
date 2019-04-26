package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//TODO: Update tests to make more comprehensive

class HandTest {

    private Hand h1, h2;
    private Card c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20;
    Card[] handa1, handi1, handa2, handi2;

    @BeforeEach
    void setUp() {
        c1 = new Card(Card.Value.ACE, Card.Suit.SPADES);
        c2 = new Card(Card.Value.NUM2, Card.Suit.HEARTS);
        c3 = new Card(Card.Value.NUM3, Card.Suit.CLUBS);
        c4 = new Card(Card.Value.NUM4, Card.Suit.DIAMONDS);
        c5 = new Card(Card.Value.NUM5, Card.Suit.SPADES);
        c6 = new Card(Card.Value.NUM6, Card.Suit.SPADES);
        c7 = new Card(Card.Value.NUM7, Card.Suit.SPADES);
        c8 = new Card(Card.Value.NUM8, Card.Suit.SPADES);
        c9 = new Card(Card.Value.NUM9, Card.Suit.SPADES);
        c10 = new Card(Card.Value.NUM10, Card.Suit.SPADES);
        c11 = new Card(Card.Value.JACK, Card.Suit.SPADES);
        c12 = new Card(Card.Value.QUEEN, Card.Suit.SPADES);
        c13 = new Card(Card.Value.KING, Card.Suit.SPADES);
        c14 = new Card(Card.Value.ACE, Card.Suit.SPADES);
        c15 = new Card(Card.Value.ACE, Card.Suit.DIAMONDS);
        c16 = new Card(Card.Value.JACK, Card.Suit.HEARTS);
        c17 = new Card(Card.Value.QUEEN, Card.Suit.HEARTS);
        c18 = new Card(Card.Value.KING, Card.Suit.HEARTS);
        c19 = new Card(Card.Value.ACE, Card.Suit.HEARTS);
        c20 = new Card(Card.Value.ACE, Card.Suit.CLUBS);
        handa1 = new Card[]{c1, c2, c3, c4, c5};
        handi1 = new Card[]{c6, c7, c8, c9, c10};
        handa2 = new Card[]{c11, c12, c13, c14, c15};
        handi2 = new Card[]{c16, c17, c18, c19, c20};
        h1 = new Hand();
        h1.addCards(Arrays.asList(handa1));
        h1.addCards(Arrays.asList(handi1));
        h1.transferActiveToInactive(Arrays.asList(handi1));
        h2 = new Hand();
        h2.addCards(Arrays.asList(handa2));
        h2.addCards(Arrays.asList(handi2));
        h2.transferActiveToInactive(Arrays.asList(handi2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkMatches() {
    }

    @Test
    void addCard() {
        List<Card> cur = new ArrayList<>(h1.getActiveCards());
        int size = h1.getNumActiveCards();
        h1.addCard(c6);
        cur.add(c6);
        assertEquals(cur, h1.getActiveCards());
        assertEquals(size+1, h1.getNumActiveCards());
    }

    @Test
    void addCards() {
        List<Card> cur = new ArrayList<>(h1.getActiveCards());
        int size = h1.getNumActiveCards();
        h1.addCards(Arrays.asList(handa2));
        cur.addAll(Arrays.asList(handa2));
        assertEquals(cur, h1.getActiveCards());
        assertEquals(size+handa2.length, h1.getNumActiveCards());
    }

    @Test
    void removeCards() {
        h1.addCard(c6);
        List<Card> cur = new ArrayList<>(h1.getActiveCards());
        h1.removeCards(Arrays.asList(handa1));
        cur.removeAll(Arrays.asList(handa1));
        assertEquals(cur, h1.getActiveCards());
        h1.removeCards(Arrays.asList(handa1));
        List<Card> rem = new ArrayList<>(cur);
        rem.add(c7);
        h1.addCard(c8);
        cur.add(c8);
        h1.removeCards(rem);
        cur.remove(c6);
        assertEquals(cur, h1.getActiveCards());
    }

    @Test
    void transferActiveToInactive() {
        h1.transferActiveToInactive(Arrays.asList(handa1));
        List<Card> combined = new ArrayList<>();
        combined.addAll(Arrays.asList(handi1));
        combined.addAll(Arrays.asList(handa1));
        assertEquals(combined, h1.getInactiveCards());
    }

    @Test
    void getActiveCards() {
        assertEquals(Arrays.asList(handa1), h1.getActiveCards());
        assertEquals(Arrays.asList(handa2), h2.getActiveCards());
        assertNotEquals(Arrays.asList(handi1), h1.getActiveCards());
        assertNotEquals(Arrays.asList(handi2), h2.getActiveCards());
    }

    @Test
    void getInactiveCards() {
        assertEquals(Arrays.asList(handi1), h1.getInactiveCards());
        assertEquals(Arrays.asList(handi2), h2.getInactiveCards());
        assertNotEquals(Arrays.asList(handa1), h1.getInactiveCards());
        assertNotEquals(Arrays.asList(handa2), h2.getInactiveCards());
    }

    @Test
    void setActiveCards() {
        h1.setActiveCards(Arrays.asList(handi1));
        assertEquals(Arrays.asList(handi1), h1.getActiveCards());
        assertNotEquals(Arrays.asList(handa1), h1.getActiveCards());
    }

    @Test
    void getNumActiveCards() {
        assertEquals(5, h1.getNumActiveCards());
        h1.addCard(c6);
        assertEquals(6, h1.getNumActiveCards());
        h1.removeCards(Arrays.asList(handa1));
        assertEquals(1, h1.getNumActiveCards());
        h1.removeCards(Arrays.asList(handa1));
        assertEquals(1, h1.getNumActiveCards());
    }

    @Test
    void getNumInactiveCards() {
        assertEquals(5, h1.getNumInactiveCards());
        h1.transferActiveToInactive(Arrays.asList(handa1));
        assertEquals(10, h1.getNumInactiveCards());
    }

    @Test
    void getNumCards() {
        assertEquals(10, h1.getNumCards());
        h1.addCard(c11);
        assertEquals(11, h1.getNumCards());
        h1.removeCards(Arrays.asList(handa1));
        assertEquals(6, h1.getNumCards());
        h1.removeCards(Arrays.asList(handa1));
        assertEquals(6, h1.getNumCards());
        h1.addCards(Arrays.asList(handa1));
        assertEquals(11, h1.getNumCards());
        h1.transferActiveToInactive(Arrays.asList(handa1));
        assertEquals(11, h1.getNumCards());
    }
}