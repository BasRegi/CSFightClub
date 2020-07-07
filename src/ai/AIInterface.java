package ai;

import data.BattleData;
import data.GameData;
import events.EventTrigger;

public abstract class AIInterface extends Thread {
	private GameData gameData;
	private BattleData battleData;
	private EventTrigger eventTrigger;

	public AIInterface(GameData gameData, EventTrigger eventTrigger) {
		this.gameData = gameData;
		this.battleData = gameData.getBattleData();
		this.eventTrigger = eventTrigger;
	}
	
	public EventTrigger getEventTrigger() {
		return eventTrigger;
	}
	/**
	 * My idea is to implement some greedy algorithm for now just to pick
	 * the card which is best suitable based on the current state of the
	 * game 
	 * It will take into account the mana available,the user's health
	 * and the AI persona's health, but also the cards available in 
	 * the deck
	 */
	
	/*
	 * Good idea !!
	 * Just do some digging on battleData and see what you can use.
	 * Dont change it!!! Robert is using BattleData.java
	 * 
	 * use eventTrigger.aiInput(String input, String ...args) as output of the AI
	 */

	public abstract void run();
}
