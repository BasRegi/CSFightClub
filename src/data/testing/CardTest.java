package data.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;

public class CardTest {
 
	DataManager dm;
	ArrayList<Card> ac;
	@Before
	public void setUp() throws Exception {
		dm = new DataManager(new GameData());
		dm.loadCards(); //Reloads all offline save files from database
		                //and puts cards into game data
		ac = dm.gameData.allCards;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void logicalEffectTest() {
		//Testing that given each appropriate "type" of card, the logicalEffect is what is expected, so will work in battle
		assertEquals("", ac.get(0).logicalEffect); //DBEffect = "Rush"
		assertEquals("",ac.get(2).logicalEffect); //DBEffect = ":"
		assertEquals("deal X damage to a random enemy minion at the end of your turn", ac.get(6).logicalEffect);
		assertEquals("gain X/X at the start of each turn", ac.get(10).logicalEffect);
		assertEquals("nullify a minion", ac.get(11).logicalEffect);
		assertEquals("give X/X to a friendly minion",ac.get(13).logicalEffect);
		assertEquals("draw X cards",ac.get(18).logicalEffect);
		assertEquals("increase the mana pool by X for this turn",ac.get(20).logicalEffect);
		assertEquals("restore X health to all minions",ac.get(23).logicalEffect);
		assertEquals("deal X damage to an enemy minion",ac.get(28).logicalEffect);
		assertEquals("deal X damage",ac.get(55).logicalEffect); //double figured quantity (10)
	}
	
	@Test
	public void quantityTest() {
		//Given each appropriate "type" of card, the quantity stored is what is expected so will work in battle
		assertEquals(3, ac.get(6).quantity1);   //Deal damage
		assertEquals(2, ac.get(18).quantity1);  //Draw card
		assertEquals(2, ac.get(20).quantity1);  //Increase mana
		assertEquals(3, ac.get(23).quantity1);  //Restore
		
		assertEquals(0, ac.get(10).quantity1);
		assertEquals(1, ac.get(10).quantity2);  //Gain
		assertEquals(3, ac.get(43).quantity1);
		assertEquals(0, ac.get(43).quantity2);  //Give
		
		assertEquals(10, ac.get(55).quantity1); //Deal double figures
		
		assertEquals(-2, ac.get(58).quantity1);
		assertEquals(3, ac.get(58).quantity2); //Give with a negative
	}
	
	@Test
	public void keywordTest() {
		//Give each appropriate "type" of card, the keyword effect stored is what is expected so will work in battle
		//And that, where only one, the second keyword variable is 'null'
		assertEquals(4,ac.get(0).keyword1); //EFFECT_RUSH = 4
		assertEquals(0,ac.get(0).keyword2);
		
		assertEquals(3,ac.get(1).keyword1); //EFFECT_DEADLINE = 3
		assertEquals(0,ac.get(1).keyword2);
		
		assertEquals(0,ac.get(2).keyword1); //EFFECT_NULL = 0   No effect at all
		assertEquals(0,ac.get(2).keyword2);
		
		assertEquals(0,ac.get(6).keyword1); //EFFECT_NULL = 0   No keyword effect only
		assertEquals(0,ac.get(6).keyword2);
		
		assertEquals(5,ac.get(7).keyword1); //EFFECT_FIRSTCALL = 5
		assertEquals(0,ac.get(7).keyword2);
		
		assertEquals(2,ac.get(32).keyword1); //EFFECT_BLOCK = 2
		assertEquals(0,ac.get(32).keyword2);
		
		assertEquals(2,ac.get(16).keyword1); //EFFECT_BLOCK = 2
		assertEquals(3,ac.get(16).keyword2); //EFFECT_DEADLINE = 3
	}
	
}
