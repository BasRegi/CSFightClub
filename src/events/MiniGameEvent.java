package events;

import data.DataManager;
import data.GameData;

public class MiniGameEvent extends SceneEvent {

	private GameData gameData;
	private DataManager dataManager;

	public MiniGameEvent(GameData gameData, DataManager dataManager) {
		this.gameData = gameData;
		this.dataManager = dataManager;
	}

	@Override
	public void start() {

		// TO DO integrate this part somewhere into events

		Event event;
		while (!gameData.sysGameEnd) {
			EventUtil.sleep(50);
			event = takeEvent();
			if (event != null) {
				if (event.getMessage().equals("startMiniGame")) {
					// go to mini game
				}
				// still to get more actions
			}
		}

	}

}
