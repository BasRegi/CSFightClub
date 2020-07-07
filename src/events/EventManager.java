package events;

import data.DataManager;
import data.GameData;
import events.battle.BattleEvent;
import events.lobby.LobbyEvent;
import gui.GUImodel;
import gui.GUIview;
import test.TestCMD;

public class EventManager {
	private SceneEvent currentSceneEvent;
	private DataManager dataManager;
	private GameData gameData;
	private EventTrigger eventTrigger;
	private GUImodel model;
	
	public EventManager(GameData gameData, DataManager dataManager){
		currentSceneEvent = null;
		this.gameData = gameData;
		this.dataManager = dataManager;
		this.eventTrigger = new EventTrigger(gameData, this);
	}
	
	public void startScene(int scene){
		switch (scene) {
		case GameData.WELCOME_PAGE:
			currentSceneEvent = new WelcomeEvent(gameData, dataManager);
			gameData.currentScene = GameData.WELCOME_PAGE;
			break;
		case GameData.HOME_PAGE:
			System.out.println("Getting into home page");
			currentSceneEvent = new HomeEvent(gameData, eventTrigger);
			gameData.currentScene = GameData.HOME_PAGE;
			break;
		case GameData.BATTLE_PAGE:
			currentSceneEvent = new BattleEvent(gameData, dataManager);
			gameData.currentScene = GameData.BATTLE_PAGE;
			break;
		case GameData.LOBBY_PAGE:
			currentSceneEvent = new LobbyEvent(gameData, model);
			gameData.currentScene = GameData.LOBBY_PAGE;
			break;
		case GameData.SETTING_PAGE:
			currentSceneEvent = new SettingsEvent(gameData, dataManager);
			gameData.currentScene = GameData.SETTING_PAGE;
			break;
		}
		if (gameData.debug) System.out.println(currentSceneEvent.getClass().getName() + " Start!");
		currentSceneEvent.start();
	}

	public void putEvent(Event event){
		currentSceneEvent.newEvent(event);
	}

	public void startGame() {
		if (gameData.debug) System.out.println("New Welcome Page");
		// TODO Call SceneManager to call SceneWelcome
		if (gameData.debug){
			TestCMD cmd = new TestCMD(gameData, eventTrigger);
			cmd.start();
			//sceneManager.loadScene(GameData.WELCOME_PAGE);
			
		}
		model = new GUImodel();
		GUIview view = new GUIview(gameData, eventTrigger, model);
		// Start Welcome Scene Events
		startScene(GameData.WELCOME_PAGE);
		
		while (!gameData.sysGameEnd) {
			EventUtil.sleep(20);
			//sceneManager.loadScene(gameData.currentScene);
			startScene(gameData.currentScene);
		}
		
	}

	public EventTrigger getEventTrigger() {
		return eventTrigger;
	}
}
