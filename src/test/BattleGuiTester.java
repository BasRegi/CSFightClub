package test;

import ai.AIRandom;
import data.BattleData;
import data.DataManager;
import data.GameData;
import events.EventManager;
import events.EventTrigger;
import events.EventUtil;
import gui.GUImodel;
import gui.GUIview;

public class BattleGuiTester extends Thread{
	
	public static GameData gameData;
	public static BattleData batData;
	public static EventTrigger eventTrigger;
	public static GUImodel model;
	public static GUIview view;

	public static void main(String[] args) {
		gameData = new GameData();
		batData = gameData.getBattleData();
		DataManager dataManager = new DataManager(gameData);
		EventManager eventManager = new EventManager(gameData, dataManager);
		eventTrigger = eventManager.getEventTrigger();
		
		System.out.println("Loading Cards from database");
		dataManager.loadCards();
		
		gameData.sysAIReady = true;
		AIRandom ai = new AIRandom(gameData, eventTrigger);
		ai.start();
		
		model = new GUImodel();
		view = new GUIview(gameData, eventTrigger, model);
		EventUtil.sleep(1000);
		new Thread(new Runnable(){
			public void run(){gameData.currentScene = GameData.BATTLE_PAGE;eventManager.startScene(GameData.BATTLE_PAGE);}
		}).start();
		
		(new BattleGuiTester()).start();
	}
	
	public void run(){
		System.out.print("Loading");
		while (!batData.myTurn && !batData.oppoTurn) {
			System.out.print(".");
			EventUtil.sleep(500);
		}
		System.out.println("\nBattle Start!");
		model.changeScreen("B");
	}

}
