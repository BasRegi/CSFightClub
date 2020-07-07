package events;

import data.GameData;

public class EventUtil {
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void endGame(GameData gameData){
		gameData.sysGameEnd = true;
		if (gameData.debug) System.out.println("Game Exit Successful");
		System.exit(0);
	}
}
