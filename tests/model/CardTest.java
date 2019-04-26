package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    private Card c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15;

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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getVal() {
        assertEquals(Card.Value.ACE, c1.getVal());
        assertEquals(Card.Value.NUM2, c2.getVal());
        assertEquals(Card.Value.NUM3, c3.getVal());
        assertEquals(Card.Value.NUM4, c4.getVal());
        assertEquals(Card.Value.NUM5, c5.getVal());
        assertEquals(Card.Value.NUM6, c6.getVal());
        assertEquals(Card.Value.NUM7, c7.getVal());
        assertEquals(Card.Value.NUM8, c8.getVal());
        assertEquals(Card.Value.NUM9, c9.getVal());
        assertEquals(Card.Value.NUM10, c10.getVal());
        assertEquals(Card.Value.JACK, c11.getVal());
        assertEquals(Card.Value.QUEEN, c12.getVal());
        assertEquals(Card.Value.KING, c13.getVal());
    }

    @Test
    void getSuit() {
        assertEquals(Card.Suit.SPADES, c1.getSuit());
        assertEquals(Card.Suit.HEARTS, c2.getSuit());
        assertEquals(Card.Suit.CLUBS, c3.getSuit());
        assertEquals(Card.Suit.DIAMONDS, c4.getSuit());
    }

    @Test
    void matches() {
        assertTrue(c1.matches(c14));
        assertTrue(c1.matches(c15));
        assertTrue(c14.matches(c1));
        assertTrue(c14.matches(c15));
        assertTrue(c15.matches(c1));
        assertTrue(c15.matches(c14));
        assertFalse(c1.matches(c2));
        assertFalse(c1.matches(c5));
        assertFalse(c2.matches(c1));
        assertFalse(c2.matches(c5));
        assertFalse(c5.matches(c1));
        assertFalse(c5.matches(c2));
    }

    @Test
    void equals() {
        assertEquals(c1, c1);
        assertEquals(c1, c14);
        assertEquals(c14, c1);
        assertNotEquals(c1, c15);
        assertNotEquals(c15, c1);
        assertNotEquals(c1, c13);
        assertNotEquals(c13, c1);
    }

    @Test
    void toStringTest(){
        assertEquals("AS", c1.toString());
        assertEquals("2H", c2.toString());
        assertEquals("3C", c3.toString());
        assertEquals("4D", c4.toString());
        assertEquals("5S", c5.toString());
        assertEquals("6S", c6.toString());
        assertEquals("7S", c7.toString());
        assertEquals("8S", c8.toString());
        assertEquals("9S", c9.toString());
        assertEquals("TS", c10.toString());
        assertEquals("JS", c11.toString());
        assertEquals("QS", c12.toString());
        assertEquals("KS", c13.toString());
        assertEquals("AS", c14.toString());
        assertEquals("AD", c15.toString());
    }
}