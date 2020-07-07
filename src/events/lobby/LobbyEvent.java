package events.lobby;

import data.GameData;
import data.LobbyData;
import events.Event;
import events.EventUtil;
import events.SceneEvent;
import gui.GUImodel;
import network.Command;

public class LobbyEvent extends SceneEvent{

	private GameData gameData;
	private LobbyData lobData;
	private GUImodel model;
	
	public LobbyEvent(GameData gameData, GUImodel model){
		this.gameData = gameData;
		this.lobData = gameData.getLobbyData();
		this.model = model;
	}
	
	public void start() {
		// TODO LobbyPage Event
		if (gameData.debug) System.out.println("DEBUG: Lobby Event Start");
		Event event;
		while (!gameData.sysGameEnd && gameData.currentScene == GameData.LOBBY_PAGE) {
			EventUtil.sleep(50);
			event = takeEvent();
			if (event!= null) {
				if (event.getMessage().equals("sendMessage")){
					String receiver = event.getArgs()[0];
					String message = event.getArgs()[1];
					sendMessage(receiver, message);
				} else if (event.getMessage().equals("receiveMessage")){
					String sender = event.getArgs()[0];
					String message = event.getArgs()[1];
					lobData.messageHistory.add(sender+":"+message);
					while (lobData.messageHistory.size()>=100) lobData.messageHistory.remove(0);
					System.out.println("receive message in the event");
					model.changeState("UpdateMessage");
				} else if (event.getMessage().equals("match")){
					matchRequest();
				} else if (event.getMessage().equals("matched")) {
					gameData.deckSeed1 = Long.valueOf(event.getArgs()[2]);
					gameData.deckSeed2 = Long.valueOf(event.getArgs()[3]);
					match(event.getArgs()[0], event.getArgs()[1]);
					break;
				} else if (event.getMessage().equals("back")){
					if (gameData.debug) System.out.println("DEBUG: Back to Home");
					lobData.client.close();
					System.out.println("out");
					gameData.currentScene = GameData.HOME_PAGE;
					return;
				} else if (event.getMessage().equals("exit")){
					exitGame();
				} else {
					if (gameData.debug) System.out.println("WARNING: LobbyPage unknown event");
				}
			}
		}
		System.out.println("LOBBY EVENT END");
	}
	
	private void matchRequest() {
		String sender = gameData.getCharactor().getName();
		Command command = new Command("MR", sender, null, null);
		lobData.client.sendMessage(command);
	}
	
	private void match(String oppoName, String whoFirst) {
		gameData.oppoName = oppoName;
		gameData.meFirst = whoFirst.equals("First") ? true : false;
		new Thread(new Runnable(){
			public void run(){model.changeState("MatchStart");}
		}).start();
		gameData.currentScene = GameData.BATTLE_PAGE;
	}

	private boolean sendMessage(String receiver, String message){
		lobData.messageSent = false;
		String type;
		if (receiver == null) type = "A"; else type = "M";
		String sender = gameData.getCharactor().getName();
		Command command = new Command(type, sender, receiver, message);
		lobData.client.sendMessage(command);
		return true;
	}

	private void exitGame() {
		EventUtil.endGame(gameData);
	}

}
