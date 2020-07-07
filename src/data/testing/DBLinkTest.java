package data.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;

public class DBLinkTest {

	@Before
	public void setUp() throws Exception {
		DataManager dm = new DataManager(new GameData());
		dm.loadCards(); //updates offline files
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void charactorTest() {
		Charactor on = DBLink.getCharactor("Player2", "password2", 1);
		Charactor off = DBLink.getCharactorOL("Player2", "password2");
		assertEquals(on.toString(),off.toString()); //haven't implemented equals
	}
	
	@Test
	public void cardsTest() {
		ArrayList<Card> on = DBLink.getAllCards();
		ArrayList<Card> off = DBLink.getAllCardsOL();
		assertEquals(on.toString(),off.toString());
	}
	
	@Test
	public void deckTest() {
		ArrayList<Integer> on1 = DBLink.loadDeck(1);
		ArrayList<Integer> off1 = DBLink.loadDeckOL(1);
		ArrayList<Integer> on2 = DBLink.loadDeck(2);
		ArrayList<Integer> off2 = DBLink.loadDeckOL(2);
		assertEquals(on1,off1);
		assertEquals(on2,off2);
	}
	
	@Test
	public void collectionTest() {
		ArrayList<DeckInfo> on = DBLink.loadPlayerDecks("Player2");
		ArrayList<DeckInfo> off = DBLink.loadPlayerDecksOL("Player2");
		assertEquals(on,off);
	}
	
	@Test //Additional test added due to last minute additional code
	public void firstGameTest() {
		assertEquals(DBLink.firstGame("Player2"),DBLink.firstGameOL("Player2"));
		assertEquals(DBLink.firstGame("Not a player"),DBLink.firstGameOL("Not a player"));
	}

}
