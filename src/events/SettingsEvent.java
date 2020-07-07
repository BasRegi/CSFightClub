package events;

import data.DataManager;
import data.GameData;

public class SettingsEvent extends SceneEvent
{
	private GameData gameData;
	private DataManager dataManager;
	
	public SettingsEvent(GameData gameData, DataManager dataManager){
		this.gameData = gameData;
		this.dataManager = dataManager;
	}
	@Override
	public void start() 
	{
		Event event;
		while (!gameData.sysGameEnd) 
		{
			EventUtil.sleep(50);
			event = takeEvent();
			if (event!= null) 
			{
				if (event.getMessage().equals("updateUsername"))
				{
					dataManager.changeUsername(gameData.getCharactor().getName(), event.getArgs()[0]);
					gameData.getCharactor().setName(event.getArgs()[0]);
				}
				if (event.getMessage().equals("updatePicID"))
				{
					dataManager.changePicture(gameData.getCharactor().getName(), Integer.parseInt(event.getArgs()[0]));
					gameData.getCharactor().setPicID(Integer.parseInt(event.getArgs()[0]));
				}
			}
		}
	}

}
