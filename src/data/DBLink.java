package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Static class used by data manager as access link to data from db and offline files
 * @author rxk592
 *
 */
public final class DBLink {

	private static Connection dbConn;
	private static DBConnect link = new DBConnect();
	
	private static File cardsFile = new File("./res/olCards.csv");
	private static File playerFile = new File("./res/olPlayers.csv");
	private static File deckFile = new File("./res/olDecks.csv");
	
	private DBLink() {
	}
	
	/**
	 * Retrieve a single card from the database
	 * @param cardID the unique Card ID
	 * @return the full Card object with that ID, will be null if no database connection
	 */
	public static Card getCard(int cardID) {
		dbConn = link.open(1);
		if(dbConn != null) {
			Card c = null;
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT c.cardID, c.name, c.cost, c.attack, c.health, c.effect, cs.type  "
							  + "FROM Card c, CardSet cs "
							  + "WHERE c.cardID = ? AND cs.id = c.cardSet"
						);
				ps.setInt(1, cardID);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) { //is there results
					rs.next();
					int test = rs.getInt(4); //if the attack is null its not a minion
					if(test == 0) {
						c = new Card(rs.getInt(1),   //so build with hex constructor
									 rs.getString(2), 
									 rs.getString(7), 
									 rs.getInt(3), 
									 rs.getString(6));
					}
					else {
						c = new Card(rs.getInt(1), 
								     rs.getString(2), 
								     rs.getString(7), 
								     rs.getInt(3), 
								     rs.getInt(4), 
								     rs.getInt(5), 
								     rs.getString(6));
					}
				}
			} catch (SQLException e) {
				System.err.println("Failed to get card from database");
				e.printStackTrace();
			} finally {
				link.close(1);
			}
			return c;  //return card, will be built if successful, null otherwise
		} else {
			return null;
		}
	}

	/**
	 * Get the data for a Player's account from the database
	 * @param username the player's unique username
	 * @param password the player's input password
	 * @param flag 0 to create new account, 1 to log in
	 * @return the Charactor object for that player, will be null of no database connection or no username-password pair
	 */
	public static Charactor getCharactor(String username, String password, int flag) {
		Charactor c = null;
		if(flag == 0) { //creating new account
			if(!doesUsernameExist(username)) { //no duplicate username
				String salt = genPWSalt();
				String hashedpw = hashPW(salt, password); //hash
				dbConn = link.open(1);
				if(dbConn != null) {
					try {
						PreparedStatement insert = 
								dbConn.prepareStatement(
										"INSERT INTO Player VALUES "
									  + "(?, ?, ?, ?, ?, ?, ?, ?)"
								);
						insert.setString(1, username);
						insert.setString(2, hashedpw);
						insert.setString(3, salt);
						insert.setInt(4, 1);
						insert.setInt(5, 0);
						insert.setInt(6, 0);
						insert.setInt(7, 0);
						insert.setInt(8, 1);
						insert.executeUpdate(); //Put in default data with account details
						c = new Charactor();
						c.setName(username); //Change user's name (as default other start-up values correct)
						System.out.println("Online creation success");
						loadDefaultDecks(username); //Put default decks for user in db
					} catch (SQLException e) {
						System.err.println("Error creating account");
						return c;
					} finally {
						link.close(1);
					}
				}
			}
		} else if (flag == 1) { //logging in
			if(validUser(username, password)) { //Entry exists with username-password pair
				dbConn = link.open(1);
				if(dbConn != null) {
					try {
						PreparedStatement ps = 
								dbConn.prepareStatement(
										"SELECT * "
									  + "FROM Player "
									  + "WHERE username = ?"
								);
						ps.setString(1, username);
						ResultSet rs = ps.executeQuery(); //Get all details
						if(rs.isBeforeFirst()) { //validUser() should enforce this but no harm
							rs.next();
							System.out.println("Online login success");
							c = new Charactor(); //Change charactor info
							c.setName(username);
							c.setYear(rs.getInt(4));
							c.setCredits(rs.getInt(5));
							c.setPlayed(rs.getInt(6));
							c.setWon(rs.getInt(7));
							c.setPicID(rs.getInt(8));
						}
					} catch (SQLException e) {
						System.err.println("Error logging in");
						return c;
					} finally {
						link.close(1);
					}
				}
			}
		}
		return c;
	}
	
	/**
	 * When creating an account, store in the database that they have both default decks
	 * @param username the unique username of the player for whom the account is being created
	 */
	private static void loadDefaultDecks(String username) {
		dbConn = link.open(1);
		if(dbConn != null) {
			try {
				PreparedStatement addPD =
						dbConn.prepareStatement(
								"INSERT INTO PlayerDeck (name, owner, collection) "
							  + "VALUES (?, ?, ?)"
						);
				addPD.setString(2,  username); //loading decks for this owner
				addPD.setString(1, "Prog" + username);
				addPD.setInt(3, 2);
		        addPD.executeUpdate(); //Programming deck
				addPD.setString(1, "Rob" + username);
				addPD.setInt(3, 3);
				addPD.executeUpdate(); //Robotics deck
				
				if(dbConn.isClosed())
					dbConn = link.open(1);
				PreparedStatement getIDs =
						dbConn.prepareStatement("SELECT deckid FROM PlayerDeck WHERE owner = ? ORDER BY deckid ASC");
				getIDs.setString(1, username);
				ResultSet ids = getIDs.executeQuery();
				ids.next();
				int id1 = ids.getInt(1);
				ids.next();
				int id2 = ids.getInt(1); //Get the deck IDs of the decks we have created
				
				if(dbConn.isClosed())
					dbConn = link.open(1);
				PreparedStatement getCards =
						dbConn.prepareStatement(
								"SELECT c.cardID FROM Card c WHERE c.cardSet = ?",
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY
						 );
				ResultSet prog;
				ResultSet rob;
				PreparedStatement insert =
						dbConn.prepareStatement(
								"INSERT INTO CardDeck VALUES (?,?)");
				
				insert.setInt(1, id1); 
				getCards.setInt(1,2);
				prog = getCards.executeQuery(); //Get the cards from programming
				prog.absolute(1);
				while(!prog.isAfterLast()) {
					insert.setInt(2, prog.getInt(1));
					insert.executeUpdate(); 
					insert.executeUpdate(); //Insert two of each card
					prog.next();
				}
				
				insert.setInt(1, id2);
				getCards.setInt(1,3);
				rob = getCards.executeQuery(); //Get the cards from robotics
				rob.absolute(1);
				while(!rob.isAfterLast()) {
					insert.setInt(2, rob.getInt(1));
					insert.executeUpdate();
					insert.executeUpdate(); 
					rob.next();
				}
				
			} catch (SQLException e) {
				System.err.println("Error loading default decks");
				e.printStackTrace();
			} finally {
				link.close(1);
			}
		}
	}

	/**
	 * Get the data for a player's account from the offline save files 
	 * @param username the unique username of the account to log in as
	 * @param password the player's input password
	 * @return the Charactor object for that player, will be null if no username-password pair
	 */
	public static Charactor getCharactorOL(String username, String password) {
		Charactor c = null;
		String hashedpw = password;
		try {
			Scanner fromPlayer = new Scanner(playerFile);
			String s = new String();
			boolean found = false;
			while(fromPlayer.hasNext() && !found) { //for every line until match found
				s = fromPlayer.nextLine();
				String[] line = parseLine(s,8); //split into array of strings
				hashedpw = hashPW(line[2], password); //hash the password
				if(line[0].equals(username) && line[1].equals(hashedpw)) {
					found = true;
					c = new Charactor(); 
					c.setName(username); //build charactor
					c.setYear(Integer.valueOf(line[3]));
					c.setCredits(Integer.valueOf(line[4]));
					c.setPlayed(Integer.valueOf(line[5]));
					c.setWon(Integer.valueOf(line[6]));
					c.setPicID(Integer.valueOf(line[7]));
				}		
			}
			fromPlayer.close();
			return c;
		} catch (FileNotFoundException e) {
			System.err.println("olPlayer.csv not found");
			return null;
		}
	}
	
	/**
	 * Check if the database contains a valid username-password pair
	 * @param username the input username
	 * @param password the input password
	 * @return true if pair exists, false otherwise
	 */
	public static boolean validUser(String username, String password) {
		if(doesUsernameExist(username)) { //if entry with that username
			dbConn = link.open(1);
			if(dbConn != null) {
				try {
					PreparedStatement ps =
							dbConn.prepareStatement(
									"SELECT salt "                
								  + "FROM Player "
								  + "WHERE username = ?"
							);
					ps.setString(1, username);
					ResultSet rs = ps.executeQuery();
					rs.next();
					String salt = rs.getString(1); //get the salt for the user
					String hashedpw = hashPW(salt, password); //hash password with salt
					dbConn = link.open(1);
					if(dbConn != null) {
						PreparedStatement find = 
								dbConn.prepareStatement(
										"SELECT username, hashedpw "
									  + "FROM Player "
									  + "WHERE username = ? AND hashedpw = ?"
								);
						find.setString(1, username);
						find.setString(2, hashedpw);
						ResultSet found = find.executeQuery();
						return found.isBeforeFirst(); //if entry in database, it means username-password pair exists
					} else {
						return false;
					}
				} catch (SQLException e) {
					System.err.println("Problem accessing Player database");
					return false;
				} finally {
					link.close(1);
				}
			} else {
				System.err.println("Failed to establish connection");
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Check whether the database already has a row with a given username
	 * @param username the username to check
	 * @return true if the username exists in the table, false otherwise
	 */
	private static boolean doesUsernameExist(String username) {
		dbConn = link.open(1);
		if(dbConn != null) {
			try {
				PreparedStatement ps = dbConn.prepareStatement("SELECT username FROM Player WHERE username = ?");
				ps.setString(1, username);
				ResultSet rs = ps.executeQuery();
				return rs.isBeforeFirst(); //if entry from query, that username exists
			} catch (SQLException e) {
				System.err.println("Error in doesUsernameExist");
			} finally {
				link.close(1);
			}
		}
		return false;
	}

	/**
	 * Load all Card objects using data from each row of database
	 * @return an ArrayList containing all the cards
	 */
	public static ArrayList<Card> getAllCards() {
		dbConn = link.open(1);
		if(dbConn != null) {
			ArrayList<Card> a = new ArrayList<Card>();
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT c.cardID, c.name, c.cost, c.attack, c.health, c.effect, cs.type "
							  + "FROM Card c, CardSet cs "
							  + "WHERE cs.id = c.cardSet "
							  + "ORDER BY c.cardID ASC"
						);
				ResultSet rs = ps.executeQuery();
				Card c;
				if(rs.isBeforeFirst()) {  //Entries in result set
					rs.next();
					while(!rs.isAfterLast()) {  //For every row
						int test = rs.getInt(4);  //if the attack is null its not a minion
						if(test == 0) {
							c = new Card(rs.getInt(1), //build hex card
										 rs.getString(2), 
										 rs.getString(7), 
										 rs.getInt(3), 
										 rs.getString(6));
						}
						else {
							c = new Card(rs.getInt(1), //build a Minion 
									     rs.getString(2), 
									     rs.getString(7), 
									     rs.getInt(3), 
									     rs.getInt(4), 
									     rs.getInt(5), 
									     rs.getString(6));
						}
						a.add(c); //add to collection
						rs.next();
					}
				}
				link.close(1);
				return a;
			} catch (SQLException e) {
				System.err.println("Problems accessing Card table");
				return null;
			} finally {
				link.close(1);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Load all Card objects using data from each row of offline save files
	 * @return an ArrayList containing all the cards
	 */
	public static ArrayList<Card> getAllCardsOL() {
		ArrayList<Card> a = new ArrayList<Card>();
		Card c;
		try {
			Scanner fromCard = new Scanner(cardsFile);
			String s =  new String();
			while(fromCard.hasNext()) {  //while still entries in file
				s = fromCard.nextLine();
				String[] line = parseLine(s, 7); //split into each field
				if(Integer.valueOf(line[3]) == 0) { //if not minion
					c = new Card(
							Integer.valueOf(line[0]),
							line[1],
							line[6],
							Integer.valueOf(line[2]),
							line[5]);
				} else {
					c = new Card(
							Integer.valueOf(line[0]),
							line[1],
							line[6],
							Integer.valueOf(line[2]),
							Integer.valueOf(line[3]),
							Integer.valueOf(line[4]),
							line[5]);
				}
				a.add(c); //add to collection
			}
			fromCard.close();
			return a;
		} catch (FileNotFoundException e) {
			System.err.println("olCards.csv not found");
			return null;
		}
	}

	/**
	 * Given a unique identifier, get all the unique Card IDs in that deck from the database
	 * @param deckID the unique identifier for the deck
	 * @return an array list of integers containing the card ids
	 */
	public static ArrayList<Integer> loadDeck(int deckID) {
		dbConn = link.open(1);
		if(dbConn != null) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT card "
							  + "FROM CardDeck "
							  + "WHERE deckID = ?"
						);
				ps.setInt(1, deckID);
				ResultSet rs = ps.executeQuery();
				if (rs.isBeforeFirst()) {
					rs.next();
					while(!rs.isAfterLast()) {
						a.add(rs.getInt(1)); //add all cards
						rs.next();
					}
				}
				link.close(1);
				return a;
			} catch (SQLException e) {
				System.err.println("Problems accessing PlayerDeck table");
				return null;
			} finally {
				link.close(1);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Given a unique identifier, get all the unique Card IDs in that deck from the offline save files
	 * @param deckID the unique identifier for the deck
	 * @return an array list of integers containing the card ids
	 */
	public static ArrayList<Integer> loadDeckOL(int deckID) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		try {
			Scanner fromDeck = new Scanner(deckFile);
			String s = new String();
			while(fromDeck.hasNext()) {
				s = fromDeck.nextLine();
				String[] line = parseLine(s, 5);
				if(Integer.valueOf(line[0]) == deckID)
					a.add(Integer.valueOf(line[4]));
			}
			fromDeck.close();
			return a;
		} catch (FileNotFoundException e) {
			System.err.println("olDecks.csv not found");
			return null;
		}
	}

	/**
	 * Given a username, retrieve data on the decks that that player owns from the database
	 * @param username the user we want to see the decks for
	 * @return an ArrayList of DeckInfo objects containing all the decks the player has
	 */
	public static ArrayList<DeckInfo> loadPlayerDecks(String username) {
		dbConn = link.open(1);
		if(dbConn != null) {
			ArrayList<DeckInfo> a = new ArrayList<DeckInfo>();
			try {
				PreparedStatement ps =
						dbConn.prepareStatement("SELECT p.deckID, p.name, p.owner, c.type "
											  + "FROM PlayerDeck p, CardSet c "
											  + "WHERE p.owner = ? AND p.collection = c.id");
				ps.setString(1, username);
				ResultSet rs = ps.executeQuery();
				if (rs.isBeforeFirst()) {
					rs.next();
					while(!rs.isAfterLast()) { //For every row of result set
						DeckInfo di = new DeckInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
						a.add(di); //add object to collection
						rs.next();
					}
				}
				link.close(1);
				return a;
			} catch (SQLException e) {
				System.err.println("Problems accessing players' decks");
				e.printStackTrace();
				return null;
			} finally {
				link.close(1);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Given a username, retrieve data on the decks that that player owns from the offline save files
	 * @param username the user we want to see the decks for
	 * @return an ArrayList of DeckInfo objects containing all the decks the player has
	 */
	public static ArrayList<DeckInfo> loadPlayerDecksOL(String username) {
		ArrayList<DeckInfo> a = new ArrayList<DeckInfo>();
		try {
			Scanner fromDeck = new Scanner(deckFile);
			String s = new String();
			String c = new String();
			while(fromDeck.hasNext()) {
				s = fromDeck.nextLine();
				String[] line = parseLine(s, 5);
				switch(Integer.valueOf(line[3])) {
				case(1) : c = "Neutral"; break; //Convert from integer which is stored into meaningful string
				case(2) : c = "Programming"; break;
				case(3) : c = "Robotics"; break;
				}
				DeckInfo di = new DeckInfo(Integer.valueOf(line[0]), line[1], line[2], c);
				if((!a.contains(di) && di.getOwner().equals(username))) //not already added and username matches
					a.add(di); //add to collection
			}
			fromDeck.close();
			return a; //return collection
		} catch (FileNotFoundException e) {
			System.err.println("olDecks.csv not found");
			return null;
		}
	}
	
	/**
	 * Use the database to find out if a given user has played a game before
	 * @param username the unique identifier for the player 
	 * @return true if they haven't played a game before
	 */
	public static boolean firstGame(String username) {
		dbConn = link.open(1);
		if(dbConn != null) {
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT played "
							  + "FROM Player "
							  + "WHERE username = ?");
				ps.setString(1, username);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) {
					rs.next();
					return (rs.getInt(1) == 0);
				} else {
					return false;
				}
			} catch (SQLException e) {
				return false;
			} finally {
				link.close(1);
			}
		} else {
			System.err.println("No database connection");
			return false;
		}
	}
	
	/**
	 * Use the offline save files to determine if a player has ever played a game before
	 * @param username the unique identifier for the player
	 * @return true if the player has never played a game, false otherwise (or if username not in file)
	 */
	public static boolean firstGameOL(String username) {
		try {
			Scanner fromPlayer = new Scanner(playerFile);
			String s;
			String[] line;
			while(fromPlayer.hasNext()) {
				s = fromPlayer.nextLine();
				line = parseLine(s,8);
				if(line[0].equals(username)) {
					fromPlayer.close();
					return Integer.valueOf(line[5]) == 0;
				}
			}
			fromPlayer.close();
			return false;
		} catch (FileNotFoundException e) {
			System.err.println("olPlayers.csv not found");
			return false;
		}
	}
	
	/**
	 * When a user plays a game, update the database to reflect the result
	 * @param username the unique username of the player who has finished a game
	 * @param won true if the player won the game, false if they lost
	 * @param levelup true if the additional credits took the player over 120, false otherwise
	 * @return true if database successfully updated, false otherwise
	 */
	public static boolean gamePlayed(String username, boolean won, boolean levelup) {
		dbConn = link.open(1);
		if(dbConn != null) {
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"UPDATE Player "
							  + "SET played = (played + 1), won = (won + ?), credits = (credits + ?) "
							  + "WHERE username = ?"
						);
				ps.setString(3, username);
				if(won) {
					ps.setInt(1, 1);
					ps.setInt(2, 10);
				} else {
					ps.setInt(1, 0);
					ps.setInt(2, 4);
				}
				ps.setString(3, username);
				int affected = ps.executeUpdate();
				boolean success = !(affected < 1); //updated one row of table, no fewer
				if(success && levelup) { //good so far, and we have to update more columns (credits and year)
					dbConn = link.open(1);
					if(dbConn != null) {
						PreparedStatement ss =
								dbConn.prepareStatement(
										"UPDATE Player "
									  + "SET credits = 0, year = (year + 1) "
									  + "WHERE username = ?"
								);
						ss.setString(1, username);
						affected = ss.executeUpdate();
						success = !(affected < 1); //update one row of table no fewer
					}
				}
				return success; //will return true if everything went as planned
			} catch (SQLException e) {
				return false;
			} finally {
				link.close(1);
			}
		} else {
			System.err.println("No database connection");
			return false;
		}
		
	}
	

	/**
	 * Change a user's username
	 * @param oldUN the current unique username of the player
	 * @param newUN the new username the player wishes to change to
	 * @return true if database successfully updated, false otherwise (e.g. no connection)
	 */
	public static boolean changeUsername(String oldUN, String newUN) {
		dbConn = link.open(1);
		if(dbConn != null) {
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT username "
							  + "FROM Player "
							  + "WHERE username = ? OR username = ?"
						);
				ps.setString(1, oldUN);
				ps.setString(2, newUN);
				ResultSet rs = ps.executeQuery();
				if(!rs.isBeforeFirst()) { //NOTHING in table (i.e. not even old username)
					return false;
				} else {
					rs.next();
					String firstResult = rs.getString(1);
					rs.next();
					if(!rs.isAfterLast()) {
						return false; //means two results, which means new username already exists
					}
					if(firstResult.equals(newUN)) {
						return false; //changed one is in, actual username isn't - something very wrong
					} else { //everything has gone well
						dbConn = link.open(1);
						if(dbConn != null) {
							PreparedStatement update =
									dbConn.prepareStatement(
											"UPDATE Player "
										  + "SET username = ? "
										  + "WHERE username = ?"
									);
							update.setString(1, newUN);
							update.setString(2, oldUN);
							return (update.executeUpdate() == 1); //will be a success if 1 row exactly is changed
						} else { //this would just be bad luck
							return false;
						}
					}
				}
			} catch (SQLException e) {
				return false;
			} finally {
				link.close(1);
			}
		} else {
			System.err.println("No database connection");
			return false;
		}
	}
	

	/**
	 * Change a user's password
	 * @param username the unique username of the player wishing to change password
	 * @param oldPW the current password of the user, as they input it
	 * @param newPW the password they wish to change to
	 * @return true if database successfully updated, false otherwise (e.g. no connection or incorrect old password)
	 */
	public static boolean changePassword(String username, String oldPW, String newPW) {
		if(validUser(username, oldPW)) { //old username-password pair exists
			String salt = genPWSalt();
			String hashedpw = hashPW(salt, newPW); //hash new password
			dbConn = link.open(1);
			if(dbConn != null) {
				try {
					PreparedStatement ps = 
							dbConn.prepareStatement(
									"UPDATE Player "
								  + "SET hashedpw = ?, salt = ? "
								  + "WHERE username = ?"
							);
					ps.setString(1, hashedpw);
					ps.setString(2, salt);
					ps.setString(3, username);
					return (ps.executeUpdate() == 1); //altered one row exactly means success
				} catch (SQLException e) {
					System.err.println("Error changing password");
					return false;
				} finally {
					link.close(1);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Change a user's picture
	 * @param username the unique username of the player wishing to change their picture
	 * @param newID the ID of the picture they wish to change to
	 * @return true if database successfully updated, false otherwise
	 */
	public static boolean changePicture(String username, int newID) {
		if(doesUsernameExist(username)) { //if we are updating a valid row
			dbConn = link.open(1);
			if(dbConn != null) {
				try {
					PreparedStatement ps =
							dbConn.prepareStatement(
									"UPDATE Player "
								  + "SET picID = ? "
								  + "WHERE username = ?"
							);
					ps.setInt(1, newID);
					ps.setString(2, username);
					return (ps.executeUpdate() == 1); //altered one row exactly means success
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				} finally {
					link.close(1);
				}
			} else {
				System.err.println("No database connection");
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Collect ALL data from the database and use it to refresh offline save files to be accurate and up-to-date
	 * @return true if files successfully updated, false otherwise (e.g. no database connection)
	 */
	public static boolean loadSaveFiles() {
		dbConn = link.open(1);
		String s;
		try {
			PrintWriter toCard = new PrintWriter(cardsFile);
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT c.cardID, c.name, c.cost, c.attack, c.health, c.effect, cs.type "
							  + "FROM Card c, CardSet cs "
							  + "WHERE cs.id = c.cardSet "
							  + "ORDER BY cardid ASC"
						);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) { 
					rs.next();
					while(!rs.isAfterLast()) { //For every entry in Card table, with info from CardSet
						s = rs.getInt(1)+","+rs.getString(2)+","+rs.getInt(3)+","+rs.getInt(4)+","+rs.getInt(5)+","+rs.getString(6)+","+rs.getString(7);
						toCard.println(s); //write all data into a line of file
						rs.next();
					}
				} else {
					System.err.println("No data collected");
				}
			} catch (SQLException e) {
				System.err.println("Error accessing DB1");
			}
			toCard.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error accessing olCards.csv");
			e.printStackTrace();
			return false;
		}
		link.close(1);
		dbConn = link.open(1);
		try {
			PrintWriter toPlayer = new PrintWriter(playerFile);
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT *"
							  + "FROM Player"
						);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) {
					rs.next();
					while(!rs.isAfterLast()) { //for each rown from Player table
						s = rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getInt(4)+","+rs.getInt(5)+","+rs.getInt(6)+","+rs.getInt(7)+","+rs.getInt(8);
						toPlayer.println(s); //write a line into csv file
						rs.next();
					}
				} else {
					System.err.println("No data collected");
				}
			} catch (SQLException e) {
				System.err.println("Error accessing DB2");
			}
			toPlayer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error accessing olPlayers.csv");
			e.printStackTrace();
			return false;
		}
		link.close(1);
		dbConn = link.open(1);
		try {
			PrintWriter toDeck = new PrintWriter(deckFile);
			try {
				PreparedStatement ps =
						dbConn.prepareStatement(
								"SELECT p.deckID, p.name, p.owner, p.collection, c.card "
							  + "FROM PlayerDeck p, CardDeck c "
							  + "WHERE p.deckID = c.deckID"
						);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) {
					rs.next();
					while(!rs.isAfterLast()) { //for each row in CardDeck, adding info from PlayerDeck
						s = rs.getInt(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getInt(4)+","+rs.getInt(5);
						toDeck.println(s); //write a line to the csv file
						rs.next();
					}
				} else {
					System.err.println("No data collected");
				}
			} catch (SQLException e) {
				System.err.println("Error accessing DB3");
				e.printStackTrace();
			}
			toDeck.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error accessing olDecks.csv");
			e.printStackTrace();
			return false;
		}
		link.close(1);
		return true;
	}
	
	/**
	 * Given a string of data separated by commas, split that into an array of Strings (who knew there was a much easier way to do this)
	 * @param s the string to split
	 * @param length the number of different elements in the string
	 * @return the array of Strings of split data
	 */
	private static String[] parseLine(String s, int length) {
		String[] line = new String[length];
		int pos = 0;
		int entry = 0;
		String field = new String();
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(pos) == ',') {
				line[entry++] = field;
				field = "";
			} else {
				field = field + s.charAt(pos);
			}
			pos++;
		}
		line[entry] = field;
		return line;
	}

	//stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
	//Copied
	/**
	 * Generate a random salt to use in hashing a user's password
	 * @return a string containing the Hex of the salt
	 */
	public static String genPWSalt() {
		byte[] salt = new byte[16];
		(new SecureRandom()).nextBytes(salt);
		return byteArrayToHexString(salt);
	}

	/**
	 * Hash the password for storage in the database so the plaintext is never stored
	 * @param salt a previously generated string of the hex salt
	 * @param pw the plaintext password to be encrypted
	 * @return a string containing the Hex of the password encrypted with the salt
	 */
	public static String hashPW(String salt, String pw) {
		KeySpec spec = new PBEKeySpec(pw.toCharArray(), hexStringToByteArray(salt), 65536, 128);
		byte[] hash;
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Missing Algorithm PBKDF2WithHmacSHA1", e);
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException("Invalid SecretKeyFactory", e);
		}
		return byteArrayToHexString(hash);
	}
	
	// Code from http://www.anyexample.com/programming/java/java%5Fsimple%5Fclass%5Fto%5Fcompute%5Fmd5%5Fhash.xml
	// Used in Intro to Computer Security module
	/**
	 * Convert from a byte array to a hex string representing that byte array
	 * @param data the byte array
	 * @return a String of the hex
	 */
	private static String byteArrayToHexString(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 

	// Code from http://javaconversions.blogspot.co.uk
	/**
	 * Convert from a string of hex to a byte array to represent that same string
	 * @param s the string to be converted
	 * @return an array of bytes 
	 */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    /**
     * For testing purposes entirely
     */
    public static void charactorTearDown() {
    	dbConn = link.open(1);
    	if(dbConn != null) {
    		try {
    			PreparedStatement ps = dbConn.prepareStatement(
    					"DELETE FROM Player "
    			      + "WHERE username = 'testplayer'");
    			ps.executeUpdate();
    		} catch (SQLException e) {
    			System.err.println("Error deleting test player");
    		} finally {
    			link.close(1);
    		}
    	}
    }


}


