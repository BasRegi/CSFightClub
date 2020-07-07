package data;

import java.util.ArrayList;

/**
 * A class to get data from the database (or offline save files)
 * and load it into the GameData, or add data to the database
 * @author rxk592
 *
 */
public class DataManager {
	public GameData gameData;
	
	public DataManager(GameData gameData){
		this.gameData = gameData;
	}
	
	/**
	 * Load all cards into GameData to limit DB access
	 * Reloads all save files if successful DB connection established
	 * @return true if successful, false otherwise
	 */
	public boolean loadCards() {
		boolean success = false;
		ArrayList<Card> cards = DBLink.getAllCards();
		if(cards == null) { //database connection not established
			System.out.println("Offline mode.");
			cards = DBLink.getAllCardsOL();
			if(cards != null) { //access to offline data
				success = true; //getting there
			}
		} else { //database connection
			success = true; //not null, so cards loaded
			DBLink.loadSaveFiles(); //update offline files
		}
		gameData.allCards = cards; //put into gamedata
		if(success) { //loaded something
			if(cards.size() == 0) //empty collection
				success = false; //actually failed
		}
		return success;
	}
	
	/**
	 * Log in to the system
	 * @param username
	 * @param password
	 * @return true if login successful, false otherwise
	 */
	public boolean login(String username, String password){
		Charactor c = DBLink.getCharactor(username, password, 1);
		if(c == null) { //Something failed in logging in
			c = DBLink.getCharactorOL(username, password);
			if(c == null) { //Must be no username-password pair
				return false; //Failure
			}
		}
		gameData.updateCharactor(c);
		return true;
	}
	
	/**
	 * Create a new account in the system
	 * @param username
	 * @param password
	 * @return true if new account created and logged in, false otherwise
	 */
	public boolean createAccount(String username, String password){
		if((username.length() != 0) && (password.length() != 0)) {
			Charactor c = DBLink.getCharactor(username, password, 0);
			if(c != null) { //Created account without issue
				gameData.updateCharactor(c);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Load a deck
	 * @param flag
	 * @param deckID
	 * @return true if successful, false otherwise
	 */
	public boolean loadDeck(int flag, int deckID) {
		boolean success = false;
		ArrayList<Integer> d = DBLink.loadDeck(deckID);
		Deck deck = null;
		if (d == null) { //no database connection 
			d = DBLink.loadDeckOL(deckID); //load from offline
			if(d != null) //access to offline data
				success = true; //getting there
		} else { //database connection
			success = true; //loaded successfully
		}
		if(success) { //got there so far
			if(d.size() > 0) { //non-empty collection
				ArrayList<Card> cards = new ArrayList<Card>();
				boolean found;
				int j;
				Card current;
				for(int i = 0; i < d.size(); i++) {
					found = false;
					j = 0;
					while(!found) {
						if(d.get(i) == gameData.allCards.get(j).getId()) {
							found = true;
							current = gameData.allCards.get(j);
							if(current.isMinion()) {
								cards.add(new Card(current.getId(), current.getName(), current.getSet(), current.getCost(), current.getAttack(), current.getDefence(), current.getDBEffect()));
							} else {
								cards.add(new Card(current.getId(), current.getName(), current.getSet(), current.getCost(), current.getDBEffect()));
							}
						}
						j++;
					}
				} //add all cards into arraylist
				deck = new Deck(deckID, cards); //load into deck
			} else { //empty collection
				success = false; //actually failed
			}
		}
		if (flag == 0) {
			gameData.getBattleData().myDeck = deck; //will be null if no success
		} else if (flag == 1) {
			gameData.getBattleData().oppoDeck = deck;
		}
		return success;
	}
	
	/**
	 * For a given player, load information on their decks into GameData.myDecks
	 * @param username the unique username of the user we are interested in
	 * @return true if the query is successful, false otherwise
	 */
	public boolean playersDecks(String username) {
		boolean success = false;
		ArrayList<DeckInfo> di = DBLink.loadPlayerDecks(username);
		if(di == null) { //no database access
			di = DBLink.loadPlayerDecksOL(username);
			if(di != null)  //access to offline data
				success = true; //getting there
		} else { //got deckinfo
			success = true;
		}
		if(success) { //loaded something
			if(di.size() == 0) //empty collection
				success = false; //actually failed
		}
		gameData.myDecks = di;
		return success;
	}
	
	/**
	 * For a player, update in the database that they have played another game and whether they won
	 * @param username the unique username of the player that played the game 
	 * @param won true if the player won the game, false otherwise
	 * @return true if database successfully updated, false where no connection so no altered record
	 */
	public boolean gamePlayed(String username, boolean won) {
		if(gameData.getCharactor().oneMoreGame(won)) { //runtime
			return DBLink.gamePlayed(username, won, true);
		} else {
			return DBLink.gamePlayed(username, won, false);
		} //database
		 
	}
	
	/**
	 * Determine whether a given Charactor has played a battle before
	 * @param username the unique identifier for the Charactor we want to know about
	 * @return true if that Charactor hasn't played a game before, false otherwise
	 */
	public boolean isFirstGame(String username) {
		if(username.equals(gameData.getCharactor().getName())) {
			return gameData.getCharactor().getPlayed() == 0;
		} else {
			if(DBLink.firstGame(username)) {
				return true;
			} else {
				return DBLink.firstGameOL(username);
			}
		}
	}
	
	
	//In these methods, I will change the Charactor info for runtime where necessary
	/**
	 * Change a user's username
	 * @param oldUN the old username
	 * @param newUN the new username
	 * @return true if change successful, false if no DB connection or new username already exists in db
	 */
	public boolean changeUsername(String oldUN, String newUN) {
		if(newUN.length() != 0) {
			boolean success = false;
			success = DBLink.changeUsername(oldUN, newUN);
			if(success) { //successful change
				gameData.getCharactor().setName(newUN); //update runtime too
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Change a user's password
	 * @param username the user 
	 * @param oldPW the user's previous password for verification
	 * @param newPW the new password they want to have (use double-entry verification before calling)
	 * @return true if password successfully changed, false if no DB connection or old password wrong
	 */
	public boolean changePassword(String username, String oldPW, String newPW) {
		if(newPW.length() != 0) {
			return DBLink.changePassword(username, oldPW, newPW); //if not successful changing, will return false
		} else {
			return false;
		}
	}
	
	/**
	 * Change a user's picture
	 * @param username the user
	 * @param newID the ID of the new picture for their account
	 * @return true if picture successfully changed, false if no DB connection
	 */
	public boolean changePicture(String username, int newID) {
		boolean success = false;
		success = DBLink.changePicture(username, newID);
		if(success) { //successfully changed in db
			gameData.getCharactor().setPicID(newID); //update for runtime
			return true;
		} else {
			return false;
		}
	}
	
}
