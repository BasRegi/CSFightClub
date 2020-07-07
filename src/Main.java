
import data.DataManager;
import data.GameData;
import events.EventManager;

public class Main {
	public static void main(String[] args){
		// Initialization
		System.out.println("Initialising...");
		GameData gameData = new GameData();
		DataManager dataManager = new DataManager(gameData);
		EventManager eventManager = new EventManager(gameData, dataManager);
		
		// Load Cards data
		if (gameData.debug) System.out.println("Loading Cards from database");
		dataManager.loadCards();
		
		// Welcome Login Scene
		if (gameData.debug) System.out.println("Game Start!\n");
		eventManager.startGame();
	}
}
