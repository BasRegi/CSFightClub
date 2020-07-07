package data.testing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;
 /**
  * Requires successful connection to database to work as account creation involved
  * @author rxk592
  *
  */
public class CharactorTest {
	
	DataManager dm;
	Charactor c;

	@Before
	public void setUp() throws Exception {
		dm = new DataManager(new GameData());
		dm.createAccount("TestPlayer", "password");
		dm.loadCards();
		c = dm.gameData.getCharactor();
	}

	@After
	public void tearDown() throws Exception {
		DBLink.charactorTearDown();
	}

	@Test
	public void test() {
		assertEquals("TestPlayer",c.getName());
		assertEquals((Integer)1,c.getYear());
		assertEquals((Integer)0,c.getCredits());
		assertEquals((Integer)0,c.getPlayed());
		assertEquals((Integer)0,c.getWon());
		assertEquals((Integer)1,c.getPicID()); //default set
		assertTrue(dm.isFirstGame("TestPlayer")); //Additional test added due to added, unplanned code
		
		assertTrue(dm.changeUsername("TestPlayer","testplayer")); //can change
		assertFalse(dm.changeUsername("TestPlayer","test")); //shouldn't exist anymore
		assertTrue(dm.gamePlayed("testplayer", true)); 
		assertTrue(dm.gamePlayed("testplayer", false)); //should be at 2 played, 1 won
		assertTrue(dm.changePicture("testplayer", 5));
		assertTrue(dm.changePassword("testplayer", "password", "password1"));
		
		assertEquals("testplayer",c.getName());
		assertEquals((Integer)2,c.getPlayed());
		assertEquals((Integer)1,c.getWon());
		assertEquals((Integer)14,c.getCredits()); //10 for win, 4 for loss
		assertEquals((Integer)1,c.getYear()); //So no levelling up
		assertEquals((Integer)5,c.getPicID());
		assertFalse(dm.isFirstGame("testplayer")); //Additional test added
		
		c.setCredits(115);
		assertTrue(dm.gamePlayed("testplayer", true)); //take above 120 credits
		assertEquals((Integer)3,c.getPlayed());
		assertEquals((Integer)2,c.getWon());
		assertEquals((Integer)2,c.getYear()); //SO levelled up to year 2
		assertEquals((Integer)0,c.getCredits()); //And got no credits again
		
		assertFalse(dm.createAccount("testplayer", "password"));
		assertFalse(dm.login("testplayer", "password"));
		assertTrue(dm.login("testplayer", "password1"));
		
		c = dm.gameData.getCharactor();
		assertEquals("testplayer",c.getName());
		assertEquals((Integer)3,c.getPlayed());
		assertEquals((Integer)2,c.getWon());
		assertEquals((Integer)2,c.getYear());
		assertEquals((Integer)0,c.getCredits());
		assertEquals((Integer)5,c.getPicID());
		
		assertTrue(dm.playersDecks("testplayer"));
		assertEquals(2,dm.gameData.myDecks.size());
		assertTrue(dm.loadDeck(0, dm.gameData.myDecks.get(0).getID()));
		assertEquals(1,dm.gameData.getBattleData().myDeck.getCard(0).getId());
		assertTrue(dm.loadDeck(0, dm.gameData.myDecks.get(1).getID()));
		assertEquals(30,dm.gameData.getBattleData().myDeck.getCard(0).getId());
	}

}
