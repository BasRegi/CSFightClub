package data.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;
import events.Event;
import events.EventSource;
import events.battle.BattleEvent;

public class BattleEventTest {

	DataManager dm1;
	BattleEvent playerBattle;
	Deck deck1 = null, deck2 = null;
	ArrayList<Card> hand1 = null, hand2 = null;
	String[] args;
	
	@Before
	public void setUp() throws Exception {
		hand1 = new ArrayList<Card>();
		hand2 = new ArrayList<Card>();
		args = new String[10];
		dm1 = new DataManager(new GameData());
		dm1.loadCards();
		playerBattle = new BattleEvent(dm1.gameData, dm1);
		playerBattle.testFlag = true;
		playerBattle.setmeFirst(true);
		playerBattle.batData.single = false;
		
		
		 
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void initialTest() {
		//Used to test that all booleans are initially set correctly before a battle occurs
		assertFalse(playerBattle.waitingForTarget);
		assertFalse(playerBattle.oppoTarget);
		assertFalse(playerBattle.myTarget);
		assertFalse(playerBattle.myWin);
		assertFalse(playerBattle.oppoWin);
		assertFalse(playerBattle.myMinion);
		assertFalse(playerBattle.oppoMinion);
		assertFalse(playerBattle.isMinion);
		assertFalse(playerBattle.blockThere);
		assertFalse(playerBattle.draw);
		assertTrue(playerBattle.testFlag);
		assertTrue(playerBattle.getmeFirst());
		assertEquals(deck1, playerBattle.batData.myDeck);
		assertEquals(deck2, playerBattle.batData.oppoDeck);
		assertEquals(40, playerBattle.batData.myHP);
		assertEquals(40, playerBattle.batData.oppoHP);
	}
	
	@Test
	public void initialDeckTest() {
		playerBattle.start();
		int myH, myD, opH, opD;
		myH = 3;
		myD = 26;
		opH = 3;
		opD = 26;
		if(playerBattle.getmeFirst()) { 
			myH++;
			myD--;
		} else {
			opH++;
			opD--;
		}
		assertEquals(myH, playerBattle.batData.myHand.size());
		assertEquals(myD, playerBattle.batData.myDeck.size());
		assertEquals(opH, playerBattle.batData.oppoHand.size());
		assertEquals(opD, playerBattle.batData.oppoDeck.size());
		
		args[0] = "0";
		
		System.out.println(playerBattle.batData.myMana);
		printCurrentHand(playerBattle.batData.myHand);
		printCurrentDeck(playerBattle.batData.myDeck);
		//TURN 1 - Player
		
		playerBattle.newEvent(new Event("selectCard", EventSource.Player, args));
		
		//assertEquals(1, playerBattle.batData.myMinions.size());
		
		//printCurrentHand(playerBattle.batData.oppoHand);
		//printCurrentDeck(playerBattle.batData.oppoDeck);
		
		playerBattle.newEvent(new Event("nextRound", EventSource.Player, args));
		//TURN 1 - Opponent
		
		args[0] = "1";
		playerBattle.newEvent(new Event("selectCard", EventSource.Opponent, args));
		
		//assertEquals(1, playerBattle.batData.oppoMinions.size());
		
		playerBattle.newEvent(new Event("nextRound", EventSource.Opponent, args));
		//TURN 2 - Player
		
		args[0] = "2";
		playerBattle.newEvent(new Event("selectCard", EventSource.Player, args));
		
		args[0] = "0";
		playerBattle.newEvent(new Event("selectMyMinion", EventSource.Player, args));
		
		//assertEquals(4, playerBattle.batData.myMinions.get(0).getAttack());
		
		playerBattle.newEvent(new Event("selectMyMinion", EventSource.Player, args));
		
		playerBattle.newEvent(new Event("selectOppoMinion", EventSource.Player, args));
		
		//assertEquals(0, playerBattle.batData.oppoMinions.size());
		//assertEquals(1, playerBattle.batData.myMinions.get(0).getDefence());
		
		playerBattle.newEvent(new Event("nextRound", EventSource.Player, args));
		//TURN 2 - Opponent
		
		args[0] = "3";
		playerBattle.newEvent(new Event("selectCard", EventSource.Opponent, args));
		
		args[0] = "0";
		playerBattle.newEvent(new Event("selectMyMinion", EventSource.Opponent, args));
		
		//assertEquals(1, playerBattle.batData.myMinions.get(0).getAttack());
		//assertEquals(4, playerBattle.batData.myMinions.get(0).getDefence());
		
		
		
	}
	
	private void printCurrentHand(ArrayList<Card> cards) {
		for(int i = 0; i < cards.size(); i ++) {
			System.out.println(cards.get(i));
		}
		System.out.println("");
	}
	
	private void printCurrentDeck(Deck deck) {
		for(int i = 0; i < deck.size(); i ++) {
			System.out.println(deck.getCard(i));
		}
		System.out.println("");
	}
}
