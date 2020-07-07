package data;

import java.util.ArrayList;

public class GameData {
	public final static int WELCOME_PAGE = 0;
	public final static int HOME_PAGE = 1;
	public final static int BATTLE_PAGE = 2;
	public final static int LOBBY_PAGE = 3;
	public final static int CHARACTOR_PAGE = 4;
	public final static int COLLECTION_PAGE = 5;
	public final static int CANVAS_PAGE = 6;
	public final static int STORE_PAGE = 7;
	public static final int SETTING_PAGE = 8;
	public static final int PAGE_PAGE = 10;
	public static final int TUTORIAL_PAGE = 9;
	
	public boolean debug = false;
	public boolean sysGameEnd = false;
	public boolean sysLockGUI = false;
	public boolean sysAIReady = false;
	public boolean sysGuiSelfStart = true;
	
	// Universal
	public boolean sysShowMsg = false;
	public String sysMsg;
	public ArrayList<String> choices;
	public ArrayList<Card> allCards;
	public ArrayList<DeckInfo> myDecks;
	
	public int frameX, frameY;
	
	// WelcomePage Data
	public String username;
	public String password;
	
	// HomePage Data
	
	// Lobby Data
	private LobbyData lobbyData = new LobbyData();
	
	// Battle Data
	public int battleTimeLimit = 30;
	private Charactor charactor = new Charactor();
	private BattleData battleData = new BattleData();
	public String oppoName;
	public long deckSeed1, deckSeed2;
	
	// Collection Data
	public int collectStart;
	public int collectEnd;
	
	public int currentScene = WELCOME_PAGE;
	public boolean meFirst;
	
	public GameData(){
		
	}

	/**
	 * Retrieve all the data about the logged in user
	 * @return the Charactor object that stores all the necessary data
	 */
	public Charactor getCharactor() {
		return charactor;
	}
	
	/**
	 * Change the data of a Charactor (to log someone new in)
	 * @param c the new Charactor object related to the user
	 */
	public void updateCharactor(Charactor c) {
		this.charactor = c;
	}
	
	/**
	 * Retrieve all the data needed to run a battle
	 * @return the BattleData object for the current battle
	 */
	public BattleData getBattleData() {
		return battleData;
	}
	
	/**
	 * Retrieve all the data needed to run a lobby
	 * @return the LobbyData object storing all the data
	 */
	public LobbyData getLobbyData(){
		return lobbyData;
	}


}
