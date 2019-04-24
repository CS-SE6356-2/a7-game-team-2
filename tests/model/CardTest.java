package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    private Card c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15;

    @BeforeEach
    void setUp() {
        c1 = new Card(Card.Suit.SPADES, Card.Value.ACE);
        c2 = new Card(Card.Suit.HEARTS, Card.Value.NUM2);
        c3 = new Card(Card.Suit.CLUBS, Card.Value.NUM3);
        c4 = new Card(Card.Suit.DIAMONDS, Card.Value.NUM4);
        c5 = new Card(Card.Suit.SPADES, Card.Value.NUM5);
        c6 = new Card(Card.Suit.SPADES, Card.Value.NUM6);
        c7 = new Card(Card.Suit.SPADES, Card.Value.NUM7);
        c8 = new Card(Card.Suit.SPADES, Card.Value.NUM8);
        c9 = new Card(Card.Suit.SPADES, Card.Value.NUM9);
        c10 = new Card(Card.Suit.SPADES, Card.Value.NUM10);
        c11 = new Card(Card.Suit.SPADES, Card.Value.JACK);
        c12 = new Card(Card.Suit.SPADES, Card.Value.QUEEN);
        c13 = new Card(Card.Suit.SPADES, Card.Value.KING);
        c14 = new Card(Card.Suit.SPADES, Card.Value.ACE);
        c15 = new Card(Card.Suit.DIAMONDS, Card.Value.ACE);
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
        assertEquals("SA", c1.toString());
        assertEquals("H2", c2.toString());
        assertEquals("C3", c3.toString());
        assertEquals("D4", c4.toString());
        assertEquals("S5", c5.toString());
        assertEquals("S6", c6.toString());
        assertEquals("S7", c7.toString());
        assertEquals("S8", c8.toString());
        assertEquals("S9", c9.toString());
        assertEquals("ST", c10.toString());
        assertEquals("SJ", c11.toString());
        assertEquals("SQ", c12.toString());
        assertEquals("SK", c13.toString());
        assertEquals("SA", c14.toString());
        assertEquals("DA", c15.toString());
    }
}