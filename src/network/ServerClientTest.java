package network;

import data.DataManager;
import data.GameData;
import events.EventManager;
import events.EventTrigger;

/**
 * Simple tests to test the connectivity of server and sending messages as well as requesting for match using MatchPool
 * @author basilregi
 *
 */
public class ServerClientTest {

	public static void main(String[] args)
	{
		//One Player
		GameData dt1 = new GameData();
		dt1.username = "Bob";
		DataManager dtmng = new DataManager(dt1);
		EventManager evmng = new EventManager(dt1, dtmng);
		EventTrigger trigger = new EventTrigger(dt1, evmng);
		Player bob = new Player(dt1, trigger, "15678", "localhost");
		//Sending message to seld through server
		Command cmd = new Command("M", bob.playerid, bob.playerid, "Bob says HI");
		bob.sendMessage(cmd);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cmd = new Command("A", "Server", "Server",  "MATCH START");
		bob.sendMessage(cmd);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Second player
		GameData dt2 = new GameData();
		dt2.username = "Steve";
		DataManager dtmng2 = new DataManager(dt2);
		EventManager evmng2 = new EventManager(dt2, dtmng2);
		EventTrigger trigger2 = new EventTrigger(dt2, evmng2);
		Player steve = new Player(dt2, trigger2, "8080", "localhost");
		//Sending message to self through server
		cmd = new Command("M", steve.playerid, steve.playerid, "Steve says HI");
		steve.sendMessage(cmd);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Bob requests for match
		cmd = new Command("MR", bob.playerid, "Server", "Request");
		bob.sendMessage(cmd);
		
		//Should print out a couple of waiting messages
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//Steve requests for match
		cmd = new Command("MR", steve.playerid, "Server", "Request");
		steve.sendMessage(cmd);

		
		//Should then match bob and steve and alert the players.
		
		//steve.close();
		//bob.close();

	}

}
