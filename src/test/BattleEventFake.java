package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import data.BattleData;
import data.Card;
import data.DataManager;
import data.Deck;
import data.GameData;
import data.Minion;
import events.Event;
import events.EventSource;
import events.EventUtil;
import events.SceneEvent;

/**
 * This is a fake battle event ... or a template
 * 
 * Robert if you have done beyond this, just use your own work.
 * Anyway this template may give you some ideas about how battle work with the other parts of the system
 * 
 * If you haven't written too much in BattleEvent, you may use this version.
 */
public class BattleEventFake extends SceneEvent {
	private GameData gameData;
	private DataManager dm;
	public BattleData batData;
	
	private EventSource firstPlayer = null;
	private int handMax = 10;
	private Deck myDeck, oppoDeck;
	
	private boolean selectTarget = false;
	private Card waitingCard = null;
	
	public BattleEventFake(GameData gameData, DataManager dm){
		this.gameData = gameData;
		this.dm = dm;
		this.batData = gameData.getBattleData();
		batData.myTurn = false;
		batData.oppoTurn = false;
	}
	
	/**
	 * Constructor of BattleEvent, allow setting up the first player
	 * @param gameData the gameData
	 * @param first the firstPlayer. EventSource.Player or EventSource.Opponent or null(default Player first)
	 */
	public BattleEventFake(GameData gameData, EventSource first){
		this.gameData = gameData;
		this.batData = gameData.getBattleData();
		this.firstPlayer = first;
	}
	
	/**
	 * The main loop of the battleEvent, keep taking event from the eventPool
	 * 
	 * initialization of the battle
	 */
	public void start(){
		if (gameData.debug) System.out.println("BattleEvent Start");
		Event event;
		
		dm.loadDeck(0, 1);
		dm.loadDeck(1, 4);
		myDeck = batData.getMyDeck();
		oppoDeck = batData.getoppoDeck();
		randomizeDeck(myDeck);
		randomizeDeck(oppoDeck);
		initBattleData();
		
		//draw 3 random cards from their decks
		drawCard(EventSource.Player, 3);
		drawCard(EventSource.Opponent, 3);
		
		//TODO get to event stage where user can choose to either pick these cards or redraw
		    // Skip 
		
		//start turns system where either you or the opponent will start
		if (firstPlayer == null || firstPlayer == EventSource.Player) {
			batData.oppoMana++;
			myRoundStart();
			myRoundMana(1);
			myRoundDraw();
			myRoundAction();
			batData.myTurn = true;
		} else {
			batData.myMana++;
			oppoRoundStart();
			oppoRoundMana(1);
			oppoRoundDraw();
			oppoRoundAction();
			batData.oppoTurn = true;
		}
		batData.battleEnd = false;
		while (!gameData.sysGameEnd && !batData.battleEnd) {
			EventUtil.sleep(10);
			event = takeEvent();
			if (event != null) {
				if (event.getMessage().equals("selectCard")) {
					selectCard(event.getArgs()[0], event.getEventSource());
				} else if (event.getMessage().equals("selectMyMinion")) {
					selectMyMinion(event.getArgs()[0]);
				} else if (event.getMessage().equals("selectOppoMinion")) {
					selectOppoMinion(event.getArgs()[0]);
				} else if (event.getMessage().equals("selectFace")) {
					selectFace();
				} else if (event.getMessage().equals("nextRound")) {
					nextRound();
				} else if (event.getMessage().equals("giveUp")) {
					giveUp();
				}
			}
		}
		
	}
	
	private void initBattleData(){
		// Should be done in BattleData!!!
		batData.myHP = 40;
		batData.myMana = 0;
		batData.myConcede = false;
		batData.myFaceSelectable = false;
		batData.myTurn = false;
		batData.myHand = new ArrayList<Card>();
		batData.myMinions = new ArrayList<Card>();
		
		batData.oppoHP = 40;
		batData.oppoMana = 0;
		batData.oppoConcede = false;
		batData.oppoFaceSelectable = false;
		batData.oppoTurn = false;
		batData.oppoHand = new ArrayList<Card>();
		batData.oppoMinions = new ArrayList<Card>();
	}

	private void giveUp() {
		batData.battleEnd = true;
		if (batData.myTurn) batData.myConcede = true;
		else batData.oppoConcede = true;
		endBattle();
	}

	private void nextRound() {
		if (batData.myTurn == true) {
			myRoundEnd();
			oppoRoundStart();
			oppoRoundMana(1);
			oppoRoundDraw();
			oppoRoundAction();
		} else if (batData.oppoTurn == true) {
			oppoRoundEnd();
			myRoundStart();
			myRoundMana(1);
			myRoundDraw();
			myRoundAction();
		} else {
			batData.battleEnd = true;
		}
	}

	private void selectFace() {
		if (batData.myFaceSelectable) {
			if (waitingCard != null) {
				// TODO take effect
				batData.myHP -= waitingCard.getAttack();
				waitingCard = null;
			}
			selectTarget = false;
		} else if (batData.oppoFaceSelectable) {
			if (waitingCard != null) {
				// TODO take effect
				batData.oppoHP -= waitingCard.getAttack();
				waitingCard = null;
			}
			selectTarget = false;
		}
	}

	private void selectMyMinion(String indexStr) {
		int index = Integer.valueOf(indexStr);
		Card minionCard = batData.myMinions.get(index);
		if (selectTarget) {
			if (waitingCard != null) {
				// TODO take effect
				minionCard.setDefence(minionCard.getDefence() - waitingCard.getAttack());
				if (minionCard.getMinion().isDestroyed()) batData.myMinions.remove(minionCard);
				waitingCard = null;
			}
			selectTarget = false;
		} else {
			// TODO immediate effect?
			// TODO if (needTarget) { selectTarget = true; waitingMinion = minion; Face selectable, minion selectable ... }
		}
		
	}

	private void selectOppoMinion(String indexStr) {
		int index = Integer.valueOf(indexStr);
		Card minionCard = batData.oppoMinions.get(index);
		if (selectTarget) {
			if (waitingCard != null) {
				// TODO take effect
				minionCard.setDefence(minionCard.getDefence() - waitingCard.getAttack());
				if (minionCard.getMinion().isDestroyed()) batData.myMinions.remove(minionCard);
				waitingCard = null;
			}
			selectTarget = false;
		} else {
			// TODO immediate effect?
			// TODO if (needTarget) { selectTarget = true; waitingMinion = minion; Face selectable, minion selectable ... }
		}
	}

	private void selectCard(String indexStr, EventSource source) {
		int index = Integer.valueOf(indexStr);
		Card card;
		if (source == EventSource.Player) {
			card = batData.myHand.get(index);
			batData.myHand.remove(index);
			batData.myMana -= card.getCost();	
		} else {
			card = batData.oppoHand.get(index);
			batData.oppoHand.remove(index);
			batData.oppoMana -= card.getCost();
		}
		updateSelectableHand(source);
		
		if (card.isMinion()) {
			card.selectable = false;
			if (source == EventSource.Player) batData.myMinions.add(card);
			else batData.oppoMinions.add(card);
		} /*else
		TODO immediate effect according to the card
		TODO if (needTarget) { selectTarget = true; waitingCard = card; Face selectable, minion selectable ... }
		*/
		
		
	}
	
	
	private void updateSelectableHand(EventSource source) {
		if (source == EventSource.Player){
			for (Card card : batData.myHand) 
				if (batData.myMana>=card.getCost()) card.selectable = true; 
				else card.selectable = false;
		} else {
			for (Card card : batData.oppoHand) 
				if (batData.oppoMana>=card.getCost()) card.selectable = true; 
				else card.selectable = false;
		}
	}

	private void myRoundStart(){
		// TODO wait for Card.java and Minion.java
		if (gameData.debug) System.out.println("::My turn start!");
	}
	
	private void myRoundMana(int manaChange){
		if (gameData.debug) System.out.println("::I get "+manaChange+" mana");
		batData.myMana += manaChange;
	}
	
	private void myRoundDraw(){
		if (gameData.debug) System.out.println("::I draw 1 card");
		drawCard(EventSource.Player, 1);
	}
	
	private void myRoundAction(){
		if (gameData.debug) System.out.println("::I can move now");
		for (Card card : batData.myHand) 
			if (batData.myMana>=card.getCost()) card.selectable = true;
		for (Card minion: batData.myMinions) minion.selectable = true;
	}
	
	private void myRoundEnd(){
		for (Card card : batData.myHand) card.selectable = false;
		for (Card minion: batData.myMinions) minion.selectable = false;
		
		// TODO End Effect?
		
		batData.myTurn = false;
		if (gameData.debug) System.out.println("::My round end");
		batData.oppoTurn = true;
	}
	
	private void oppoRoundStart(){
		// TODO Start Event
		if (gameData.debug) System.out.println("::Opponent turn start!");
	}
	
	private void oppoRoundMana(int manaChange){
		if (gameData.debug) System.out.println("::Opponent get "+manaChange+" mana");
		batData.oppoMana += manaChange;
	}
	
	private void oppoRoundDraw(){
		if (gameData.debug) System.out.println("::Opponent draw 1 card");
		drawCard(EventSource.Opponent, 1);
	}
	
	private void oppoRoundAction(){
		if (gameData.debug) System.out.println("::Opponent can start to move");
		for (Card card : batData.oppoHand) 
			if (batData.oppoMana>=card.getCost()) card.selectable = true;
		for (Card minion: batData.oppoMinions) minion.selectable = true;
	}
	
	private void oppoRoundEnd(){
		for (Card card : batData.oppoHand) card.selectable = false;
		for (Card minion: batData.oppoMinions) minion.selectable = false;
		
		// TODO End Effect?
		
		batData.oppoTurn = false;
		if (gameData.debug) System.out.println("::Opponent round end");
		batData.myTurn = true;
	}
	
	private void drawCard(EventSource source, int num){
		if (source == EventSource.Player){
			for (int i=0; i<num; i++){
				batData.myHand.add(myDeck.getCard(0));
				myDeck.removeCard(0);
			}
		}else{
			for (int i=0; i<num; i++){
				batData.oppoHand.add(oppoDeck.getCard(0));
				oppoDeck.removeCard(0);
			}
		}
	}
	
	private void randomizeDeck(Deck deck) {
		long seed = System.nanoTime();
		Collections.shuffle(deck.getCards(), new Random(seed));
	}
	
	private void endBattle(){
		batData.battleEnd = true;
		batData.myTurn = false;
		batData.oppoTurn = false;
		gameData.currentScene = GameData.HOME_PAGE;
	}
	
}