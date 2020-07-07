package events;

import ai.AIRandom;
import data.GameData;
import network.Player;

public class HomeEvent extends SceneEvent {
	private GameData gameData;
	private EventTrigger eventTrigger;
	
	public HomeEvent(GameData gameData, EventTrigger eventTrigger){
		this.gameData = gameData;
		this.eventTrigger = eventTrigger;
	}
	
	public void start(){
		if (gameData.debug) System.out.println("Home Event Start");
		Event event;
		while (!gameData.sysGameEnd && gameData.currentScene == GameData.HOME_PAGE) {
			EventUtil.sleep(50);
			event = takeEvent();
			if (event!= null) {
				
				if (event.getMessage().equals("clickLobby")){
					String host = event.getArgs()[0];
					String port = event.getArgs()[1];
					if (intoLobby(host, port)) break;
				} else if (event.getMessage().equals("clickSingle")) {
					intoSingle();
					break;
				} else if (event.getMessage().equals("clickCollection")){
					intoCollection();
				} else if (event.getMessage().equals("clickCanvas")){
					intoCanvas();
				} else if (event.getMessage().equals("clickSettings")){
					intoSettings();
				} else if (event.getMessage().equals("exit")){
					exitGame();
				} else {
					
				}
			}
		}
	}
	
	private void intoSingle() {
		// TODO Auto-generated method stub
		/*
		if (gameData.sysAIReady) {
			AIRandom ai = new AIRandom(gameData, eventTrigger);
			ai.start();
			gameData.getBattleData().single = true;
		}
		*/
		gameData.currentScene = GameData.BATTLE_PAGE;
	}

	private boolean intoLobby(String host, String port){
		// TODO home page intoLobby
		gameData.sysLockGUI = true;
		if (gameData.debug) {System.out.println("Try to connect "+host+":"+port);}
		Player client;
		try
		{
			client = new Player(gameData, eventTrigger, port, host);
			if (!client.connect()) {throw new Exception("Server Connection Failed");}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			gameData.sysLockGUI = false;
			return false;
		}
		gameData.getLobbyData().client = client;
		gameData.currentScene = GameData.LOBBY_PAGE;
		gameData.sysLockGUI = false;
		return true; 
	}
	
	private void intoCollection(){
		// TODO home page intoCollection 
		if (gameData.debug) System.out.println("Collection Not implemented yet");
	}
	
	private void intoCanvas(){
		// TODO home page intoCanvas
		if (gameData.debug) System.out.println("Canvas Not implemented yet");
	}
	
	private void intoSettings(){
		// TODO home page intoSettings
		if (gameData.debug) System.out.println("Settings Not implemented yet");
	}
	
	private void exitGame(){
		EventUtil.endGame(gameData);
	}
}
