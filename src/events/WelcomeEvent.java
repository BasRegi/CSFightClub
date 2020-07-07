package events;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import data.DataManager;
import data.GameData;

public class WelcomeEvent extends SceneEvent {
	private GameData gameData;
	private DataManager dataManager;
	
	public WelcomeEvent(GameData gameData, DataManager dataManager){
		this.gameData = gameData;
		this.dataManager = dataManager;
	}
	
	public void start() {
		// TODO Auto-generated method stub		
		Event event;
		while (!gameData.sysGameEnd) {
			EventUtil.sleep(50);
			event = takeEvent();
			if (event!= null) {
				if (event.getMessage().equals("changeBoxUsername")){
					changeBoxUsername(event.getArgs()[0]);
					
				} else if (event.getMessage().equals("changeBoxPassword")){
					changeBoxPassword(event.getArgs()[0]);
					
				} else if (event.getMessage().equals("login")) {
					if (login(event.getArgs()[0], event.getArgs()[1])==true) break;
					else 
					{
						UIManager.put("OptionPane.okButtonText", "OK");
						JOptionPane.showMessageDialog(null, "Incorrect Username/Password", "Error", JOptionPane.PLAIN_MESSAGE);
					}
					
				} else if (event.getMessage().equals("register")){
					register(event.getArgs()[0], event.getArgs()[1]);
					
				} else if (event.getMessage().equals("exit")){
					exitGame();
				} else
				if (gameData.debug) System.out.println("WARNING: WelcomePage accept Start only");
			}
		}
	}
	
	private void changeBoxUsername(String username){
		// TODO changeBoxUsername Event
		gameData.username = username;
	}
	
	private void changeBoxPassword(String password){
		// TODO chagneBoxPassword Event
		gameData.password = password;
	}
	
	private boolean register(String username, String password){
		// TODO register Event
		//if (gameData.debug) System.out.println(username + ":" + password);
		gameData.sysLockGUI = true;
		gameData.username = username;
		gameData.password = password;
		if (dataManager.createAccount(username, password) == true){
			gameData.sysLockGUI = false;
			gameData.currentScene = GameData.HOME_PAGE;
			return true;
		} else {
			gameData.sysLockGUI = false;
			return false;
		}
	}
	
	private boolean login(String username, String password){
		// TODO login Event
		//if (gameData.debug) System.out.println(username + ":" + password);
		gameData.sysLockGUI = true;
		gameData.username = username;
		gameData.password = password;
		if (dataManager.login(username, password) == true){
			gameData.sysLockGUI = false;
			gameData.currentScene = GameData.HOME_PAGE;
			return true;
		} else {
			if (gameData.debug) System.out.println("Incorrect username or password");
			gameData.sysLockGUI = false;
			return false;
		}
	}
	
	private void exitGame(){
		EventUtil.endGame(gameData);
	}
}
