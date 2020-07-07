package events;

import data.GameData;
import events.Event;
import network.Command;

public class EventTrigger {
	private EventManager eventManager;
	private GameData gameData;
	// TODO
	
	public EventTrigger(GameData gameData, EventManager eventManager){
		this.gameData = gameData;
		this.eventManager = eventManager;
	}
	
	public void aiInput(String input, String ...args){
		if (gameData.debug) System.out.println("DEBUG: eventTrigger got AI event \""+input+"\"");
		switch (gameData.currentScene) {
		case GameData.BATTLE_PAGE:
			processBattlePage(input, EventSource.Opponent, args);
			break;
		case GameData.LOBBY_PAGE:
			processLobbyPage(input, args);
		}
	}
	
	public void myInput(String input, String ...args){
		if (gameData.debug) System.out.println("DEBUG: eventTrigger got my event \""+input+"\"");
		switch (gameData.currentScene) {
		case GameData.WELCOME_PAGE: 
			processWelcomePage(input, args);
			break;
		case GameData.HOME_PAGE:
			processHomePage(input, args);
			break;
		case GameData.BATTLE_PAGE:
			processBattlePage(input, EventSource.Player, args);
			break;
		case GameData.LOBBY_PAGE:
			processLobbyPage(input, args);
			break;
		case GameData.SETTING_PAGE:
			processSettingPage(input, args);
			break;
		}
	}
	
	

	public void netInput(String input, String ...args){
		if (gameData.debug) System.out.println("DEBUG: eventTrigger got event \""+input+"\"");
		switch (gameData.currentScene) {
		case GameData.BATTLE_PAGE:
			processBattlePage(input, EventSource.Opponent, args);
			break;
		case GameData.LOBBY_PAGE:
			processLobbyPage(input, args);
		}
	}
	
	public void sysInput(String input, String ...args){
		if (gameData.debug) System.out.println("DEBUG: eventTrigger got event \""+input+"\"");
		switch (gameData.currentScene) {
		case GameData.BATTLE_PAGE:
			processBattlePage(input, EventSource.GameSystem, args);
			break;
		case GameData.LOBBY_PAGE:
			processLobbyPage(input, args);
		}
	}
	
	private void processWelcomePage(String input, String ...args) {
		if (input.equals("changeBoxUsername")
			||input.equals("changeBoxPassword")
			|| input.equals("register")
			|| input.equals("login")
			|| input.equals("exit")){
			eventManager.putEvent(new Event(input, EventSource.Player, args));
		} else {
			if (gameData.debug) System.out.println("Mick! you write the wrong eventTrigger message for welcomePage, there is no "+input);
		}
	}
	
	private void processSettingPage(String input, String ...args)
	{
		if(input.equals("updateUsername")
			|| input.equals("updatePicID"))
			eventManager.putEvent(new Event(input, EventSource.Player, args));
		else
		{
			if (gameData.debug) System.out.println("Basil! you write the wrong eventTrigger message for welcomePage, there is no "+input);
		}
	}

	private void processHomePage(String input, String ...args) {
		if (input.equals("clickLobby")
				|| input.equals("clickSingle")
				|| input.equals("clickCollection")
				|| input.equals("clickCanvas")
				|| input.equals("clickSettings")
				|| input.equals("exit")){
				eventManager.putEvent(new Event(input, EventSource.Player, args));
			} else {
				if (gameData.debug) System.out.println("Mick! you write the wrong eventTrigger message for HomePage, there is no "+input);
			}
		
	}
	
	private void processBattlePage(String input, EventSource source, String ...args) {
		System.out.println(input+" from "+source);
		if (input.equals("selectCard") 
				|| input.equals("selectMyMinion")
				|| input.equals("selectOppoMinion")
				|| input.equals("selectMyFace")
				|| input.equals("selectOppoFace")
				|| input.equals("nextRound")
				|| input.equals("giveUp")){
			eventManager.putEvent(new Event(input, source, args));
			if (input.equals("selectCard")  && source == EventSource.Player) {
				System.out.println(gameData.getBattleData().myHand.get(Integer.valueOf((args[0]))) + ", " + Integer.valueOf(args[0]));
			} else if (input.equals("selectCard")  && source == EventSource.Player) {
				System.out.println(gameData.getBattleData().myHand.get(Integer.valueOf((args[0]))) + ", " + Integer.valueOf(args[0]));
			} else if (input.equals("selectMyMinion")  && source == EventSource.Player) {
				System.out.println(gameData.getBattleData().myMinions.get(Integer.valueOf((args[0]))) + ", " + Integer.valueOf(args[0]));
			} else if (input.equals("selectOppoMinion")  && source == EventSource.Player) {
				System.out.println(gameData.getBattleData().oppoMinions.get(Integer.valueOf((args[0]))) + ", " + Integer.valueOf(args[0]));
			} else if (source == EventSource.GameSystem) {
				System.out.println(input + source + Integer.valueOf(args[0]));
			}
			if (!gameData.getBattleData().single && source==EventSource.Player){
				System.out.println("network sending "+input+(args.length==0 ? "" : (":" + args[0])));
				if (input.equals("selectMyMinion")) input = "selectOppoMinion";
				else if (input.equals("selectOppoMinion")) input = "selectMyMinion";
				else if (input.equals("selectMyFace")) input = "selectOppoFace";
				else if (input.equals("selectOppoFace")) input = "selectMyFace";
				Command command = new Command("BM", gameData.getCharactor().getName(), gameData.oppoName, 
						input+(args.length==0 ? "" : (":" + args[0])));
				gameData.getLobbyData().client.sendMessage(command);
			}
		}
	}
	
	private void processLobbyPage(String input, String[] args) {
		if (input.equals("sendMessage")
				|| input.equals("receiveMessage")
				|| input.equals("match")
				|| input.equals("matched")
				|| input.equals("intoSloman")
				|| input.equals("back")
				|| input.equals("exit")){
			eventManager.putEvent(new Event(input, 
					EventSource.Player, args));
		} else {
			if (gameData.debug) System.out.println("Mick! you write the wrong eventTrigger message for LobbyPage, there is no "+input);
		}
				
	}
}
