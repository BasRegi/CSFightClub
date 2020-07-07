package data;

import java.sql.*;
/**
 * 
 * @author rxk592
 * A class to record that I have designed the database so I
 * have a concrete contribution to the project
 * (And also so it can be re-used to make BIG updates
 * to the database)
 */
public class DataSetup {
	
	public static void main(String[] args) {
		DBConnect link = new DBConnect();
		Connection dbConn = null;
		dbConn = link.open(1);
		refreshTables(dbConn);
		buildPlayerTable(dbConn);
		buildCardSetTable(dbConn);
		buildCardTable(dbConn);
		buildPlayerDecksTable(dbConn);
		buildCardDecksTable(dbConn);
		insertTestData(dbConn);
		link.close(1);
	}

	/**
	 * Drop all the relevant existing relations for a clean rebuild 
	 * @param dbConn database Connection object
	 */
	private static void refreshTables(Connection dbConn) {
		try {
			Statement s1 = dbConn.createStatement();
			s1.executeUpdate("DROP TABLE CardDeck");
			s1.executeUpdate("DROP TABLE PlayerDeck");
			s1.executeUpdate("DROP TABLE Player");
			s1.executeUpdate("DROP TABLE Card");
			s1.executeUpdate("DROP TABLE CardSet");
			s1.close();
		} catch (SQLException e) {
			System.out.println("Failed to drop tables");
			e.printStackTrace();
		}
	}

	/**
	 * Build the Player table
	 * @param dbConn database Connection object
	 */
	private static void buildPlayerTable(Connection dbConn) {
		try {
			PreparedStatement player =
					dbConn.prepareStatement(
							"CREATE TABLE Player( "
						  + "username VARCHAR NOT NULL UNIQUE, "
						  + "hashedPW VARCHAR NOT NULL, "
						  + "salt VARCHAR NOT NULL, "
						  + "year INTEGER NOT NULL, "
						  + "credits INTEGER NOT NULL, "
						  + "played INTEGER NOT NULL, "
						  + "won INTEGER NOT NULL, "
						  + "picID INTEGER NOT NULL, "
						  + "PRIMARY KEY (username))"
						  );
			player.executeUpdate();
			player.close();
		} catch (SQLException e) {
			System.out.println("Failed to add Player table.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Build the CardSet table
	 * @param dbConn database Connection object
	 */
	private static void buildCardSetTable(Connection dbConn) {
		try {
			PreparedStatement cardSet =
					dbConn.prepareStatement(
							"CREATE TABLE CardSet( "
						  + "id SERIAL NOT NULL UNIQUE, "
						  + "type VARCHAR NOT NULL, "
						  + "PRIMARY KEY (id)"
						  + ") "
						  );
			cardSet.executeUpdate();
			cardSet.close();
		} catch(SQLException e) {
			System.out.println("Failed to add CardSet table");
			e.printStackTrace();
		}
	}
	
	/**
	 * Build the Card table
	 * @param dbConn database Connection object
	 */
	private static void buildCardTable(Connection dbConn) {
		try {
			PreparedStatement card =
					dbConn.prepareStatement(
							"CREATE TABLE Card( "
						  + "cardID SERIAL NOT NULL UNIQUE, "
						  + "name VARCHAR NOT NULL, "
						  + "cardSet INTEGER NOT NULL, "
						  + "cost INTEGER NOT NULL, "
						  + "attack INTEGER, "
						  + "health INTEGER, "
						  + "effect VARCHAR, "
						  + "PRIMARY KEY (cardID), "
						  + "FOREIGN KEY (cardSet) REFERENCES CardSet(id) "
						  + "	ON DELETE RESTRICT"
						  + ") "
						  );
			card.executeUpdate();
			card.close();
		} catch (SQLException e) {
			System.out.println("Failed to add Card table");
			e.printStackTrace();
		}
	}

	/**
	 * Build the PlayerDeck table
	 * @param dbConn database Connection object
	 */
	private static void buildPlayerDecksTable(Connection dbConn) {
		try {
			PreparedStatement playerDeck =
					dbConn.prepareStatement(
							"CREATE TABLE PlayerDeck( "
						  + "deckID SERIAL NOT NULL UNIQUE, "
						  + "name VARCHAR NOT NULL, "
						  + "owner VARCHAR NOT NULL, "
						  + "collection INTEGER NOT NULL, "
						  + "PRIMARY KEY (deckID), "
						  + "FOREIGN KEY (owner) REFERENCES Player(username) "
						  + "	ON DELETE CASCADE ON UPDATE CASCADE, "
						  + "FOREIGN KEY (collection) REFERENCES CardSet(id) "
						  + "	ON DELETE RESTRICT"
						  + ") "
						  );
			playerDeck.executeUpdate();
			playerDeck.close();
		} catch (SQLException e) {
			System.out.println("Failed to add PlayerDeck table");
			e.printStackTrace();
		}
	}

	/**
	 * Build the CardDeck table
	 * @param dbConn database Connection object
	 */
	private static void buildCardDecksTable(Connection dbConn) {
		try {
			PreparedStatement  cardDeck =
					dbConn.prepareStatement(
							"CREATE TABLE CardDeck( "
						  + "deckID INTEGER NOT NULL, "
						  + "card INTEGER NOT NULL, "
						  + "FOREIGN KEY (deckID) REFERENCES PlayerDeck(deckID) "
						  + "	ON DELETE CASCADE, "
						  + "FOREIGN KEY (card) REFERENCES Card(cardID) "
						  + "	ON DELETE RESTRICT"
						  + ") "
						  );
			cardDeck.executeUpdate();
			cardDeck.close();
		} catch (SQLException e) {
			System.out.println("Failed to add CardDeck table");
			e.printStackTrace();
		}
	}
	
	/**
	 * Almost redundant helper method to make it look cleaner
	 * @param dbConn database Connection object
	 */
	private static void insertTestData(Connection dbConn) {
		insertCardSets(dbConn);
		insertCards(dbConn);
		insertPlayers(dbConn);
		insertPlayerDecks(dbConn);
		insertCardDecks(dbConn);
	}

	/**
	 * Fill with card set data
	 * @param dbConn database Connection object
	 */
	private static void insertCardSets(Connection dbConn) {
		try {
			PreparedStatement s = 
					dbConn.prepareStatement(
							"INSERT INTO CardSet (type) VALUES "
						  + "(?)"
					);
			s.setString(1, "Neutral");
			s.executeUpdate();
			s.setString(1, "Programming");
			s.executeUpdate();
			s.setString(1, "Robotics");
			s.executeUpdate();
			s.close();
		}
		catch (SQLException e) {
			System.out.println("Failed to add one or more CardSets");
			e.printStackTrace();
		}
	}

	/**
	 * Fill with card data
	 * @param dbConn database Connection object
	 */
	private static void insertCards(Connection dbConn) {
		try {
			PreparedStatement s = 
					dbConn.prepareStatement(
							"INSERT INTO Card (name, cardSet, cost, attack, health, effect) VALUES "
						  + "(?, ?, ?, ?, ?, ?)"
					);
			s.setInt(2, 2); //Inserting programming cards
			s.setString(1,  "ActiveX");
			s.setInt(3, 1);
			s.setInt(4, 1);
			s.setInt(5, 1);
			s.setString(6,  "Rush"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Fortran");
			s.setInt(3, 1);
			s.setInt(4, 1);
			s.setInt(5, 2);
			s.setString(6,  "Deadline: Deal 1 damage to a random enemy minion"); //logicalEffect = "deal X damage to a random enemy minion"
			s.executeUpdate();
			s.setString(1,  "F#");
			s.setInt(3, 2);
			s.setInt(4, 3);
			s.setInt(5, 2);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "LISA");
			s.setInt(3, 3);
			s.setInt(4, 3);
			s.setInt(5, 4);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "JADE");
			s.setInt(3, 4);
			s.setInt(4, 4);
			s.setInt(5, 5);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Swift");
			s.setInt(3, 4);
			s.setInt(4, 4);
			s.setInt(5, 2);
			s.setString(6,  "Rush"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Ada");
			s.setInt(3, 7);
			s.setInt(4, 7);
			s.setInt(5, 5);
			s.setString(6,  ":Deal 3 damage to a random enemy minion at the end of your turn"); //logicalEffect = "deal X damage to a random enemy minion at the end of your turn"
			s.executeUpdate();
			s.setString(1,  "Java");
			s.setInt(3, 6);
			s.setInt(4, 5);
			s.setInt(5, 5);
			s.setString(6,  "First Call: Deal 1 damage to all enemy minions"); //logicalEffect = "deal X damage to all enemy minions"
			s.executeUpdate();
			s.setString(1,  "Ruby");
			s.setInt(3, 5);
			s.setInt(4, 3);
			s.setInt(5, 4);
			s.setString(6,  "First Call: Deal 3 damage"); //logicalEffect = "deal X damage"
			s.executeUpdate();
			s.setString(1,  "C");
			s.setInt(3, 3);
			s.setInt(4, 2);
			s.setInt(5, 2);
			s.setString(6,  ":Gain +1/+1 at the end of your turn"); //logicalEffect = "gain X/X at the end of your turn"
			s.executeUpdate();
			s.setString(1,  "C++");
			s.setInt(3, 5);
			s.setInt(4, 4);
			s.setInt(5, 5);
			s.setString(6,  ":Gain +0/+1 at the start of each turn"); //logicalEffect = "gain X/X at the start of each turn"
			s.executeUpdate();
			s.setString(1,  "Haskell");
			s.setInt(3, 6);
			s.setInt(4, 6);
			s.setInt(5, 5);
			s.setString(6,  "First Call: Nullify a minion"); //logicalEffect = "nullify a minion"
			s.executeUpdate();
			s.setString(1,  "OCaml");
			s.setInt(3, 4);
			s.setInt(4, 4);
			s.setInt(5, 4);
			s.setString(6,  "First Call: Deal 2 damage to a random enemy minion"); //logicalEffect = "deal X damage to a random enemy minion"
			s.executeUpdate();
			s.setString(1,  "Assembly");
			s.setInt(3, 3);
			s.setInt(4, 3);
			s.setInt(5, 2);
			s.setString(6,  "First Call: Give +1/+1 to a friendly minion"); //logicalEffect = "give X/X to a friendly minion"
			s.executeUpdate();
			s.setString(1,  "Basic");
			s.setInt(3, 1);
			s.setInt(4, 1);
			s.setInt(5, 1);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "JavaScript");
			s.setInt(3, 2);
			s.setInt(4, 2);
			s.setInt(5, 3);
			s.setString(6,  "First Call: Deal 1 damage"); //logicalEffect = "deal X damage"
			s.executeUpdate();
			s.setString(1,  "HTML");
			s.setInt(3, 8);
			s.setInt(4, 3);
			s.setInt(5, 7);
			s.setString(6,  "Block; Deadline: Destroy all minions"); //logicalEffect = "destroy all minions"
			s.executeUpdate();
			s.setString(1,  "PHP");
			s.setInt(3, 8);
			s.setInt(4, 8);
			s.setInt(5, 8);
			s.setString(6,  ":"); //logicalEffect = "destroy all minions"
			s.executeUpdate();
			s.setString(1,  "Perl");
			s.setInt(3, 3);
			s.setInt(4, 1);
			s.setInt(5, 2);
			s.setString(6,  "First Call: Draw 2 cards"); //logicalEffect = "draw X cards"
			s.executeUpdate();
			s.setNull(4, Types.INTEGER); //Doing hexes now
			s.setNull(5, Types.INTEGER);
			s.setString(1, "Abstraction");
			s.setInt(3, 2);
			s.setString(6, ":Draw a card"); //logicalEffect = "draw a card"
			s.executeUpdate();
			s.setString(1, "Address Space");
			s.setInt(3, 0);
			s.setString(6, ":Increase the mana pool by 2 for this turn"); //logicalEffect = "increase the mana pool by X for this turn"
			s.executeUpdate();
			s.setString(1, "Comment");
			s.setInt(3, 5);
			s.setString(6, ":Give +4/+4 to a minion"); //logicalEffect = "give X/X to a minion"
			s.executeUpdate();
			s.setString(1, "Dump");
			s.setInt(3, 6);
			s.setString(6, ":Destroy a minion"); //logicalEffect = "destroy a minion"
			s.executeUpdate();
			s.setString(1, "Compile");
			s.setInt(3, 4);
			s.setString(6, ":Restore 3 health to all minions"); //logicalEffect = "restore X health to all minions"
			s.executeUpdate();
			s.setString(1, "Conversion");
			s.setInt(3, 1);
			s.setString(6, ":Swap attack/health of a minion"); //logicalEffect = "swap attack/health of a minion"
			s.executeUpdate();
			s.setString(1, "Curse-r");
			s.setInt(3, 4);
			s.setString(6, ":Deal 6 damage"); //logicalEffect = "deal X damage"
			s.executeUpdate();
			s.setString(1, "Extension");
			s.setInt(3, 2);
			s.setString(6, ":Restore 4 health"); //logicalEffect = "restore X health"
			s.executeUpdate();
			s.setString(1, "Firewall");
			s.setInt(3, 3);
			s.setString(6, ":Deal 3 damage to all minions"); //logicalEffect = "deal X damage to all minions"
			s.executeUpdate();
			s.setString(1, "Trojan");
			s.setInt(3, 4);
			s.setString(6, ":Deal 7 damage to an enemy minion"); //see google doc //logicalEffect = "deal X damage to an enemy minion"
			s.executeUpdate();																	
			
			s.setInt(2, 3); //Inserting robotics cards
			s.setString(1,  "Lego NXT");
			s.setInt(3, 2);
			s.setInt(4, 2);
			s.setInt(5, 1);
			s.setString(6,  "Deadline: Draw a card"); //logicalEffect = "draw a card"
			s.executeUpdate();
			s.setString(1,  "Sandflea");
			s.setInt(3, 2);
			s.setInt(4, 3);
			s.setInt(5, 1);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Roomba");
			s.setInt(3, 3);
			s.setInt(4, 2);
			s.setInt(5, 2);
			s.setString(6,  "First Call: Deal 4 damage to an enemy minion"); //logicalEffect = "deal X damage to an enemy minion"
			s.executeUpdate();
			s.setString(1,  "Sojourner");
			s.setInt(3, 6);
			s.setInt(4, 6);
			s.setInt(5, 7);
			s.setString(6,  "Block"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Colossus");
			s.setInt(3, 8);
			s.setInt(4, 7);
			s.setInt(5, 7);
			s.setString(6,  ":Deal 2 damage to all characters"); //logicalEffect = "deal X damage to all characters"
			s.executeUpdate();
			s.setString(1,  "Betty");
			s.setInt(3, 5);
			s.setInt(4, 4);
			s.setInt(5, 5);
			s.setString(6,  "Block"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Bob");
			s.setInt(3, 5);
			s.setInt(4, 5);
			s.setInt(5, 3);
			s.setString(6,  "Rush"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "C.P.U");
			s.setInt(3, 10);
			s.setInt(4, 4);
			s.setInt(5, 4);
			s.setString(6,  "Deadline: Deal 8 damage to your opponent"); //logicalEffect = "deal X damage to your opponent"
			s.executeUpdate();
			s.setString(1,  "The mother of boards");
			s.setInt(3, 8);
			s.setInt(4, 6);
			s.setInt(5, 7);
			s.setString(6,  ":Restore 1 health to all friendly minions at the end of your turn"); //logicalEffect = "restore X health to all friendly minions at the end of your turn"
			s.executeUpdate();
			s.setString(1,  "Mouse");
			s.setInt(3, 1);
			s.setInt(4, 1);
			s.setInt(5, 2);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Engine");
			s.setInt(3, 6);
			s.setInt(4, 6);
			s.setInt(5, 6);
			s.setString(6,  ":"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Asimo");
			s.setInt(3, 3);
			s.setInt(4, 3);
			s.setInt(5, 3);
			s.setString(6,  "First Call: Nullify a minion"); //logicalEffect = "nullify a minion"
			s.executeUpdate();
			s.setString(1,  "Wildcat");
			s.setInt(3, 4);
			s.setInt(4, 4);
			s.setInt(5, 1);
			s.setString(6,  "Rush"); //logicalEffect = ""
			s.executeUpdate();
			s.setString(1,  "Curiosity Rover");
			s.setInt(3, 7);
			s.setInt(4, 4);
			s.setInt(5, 8);
			s.setString(6,  "Block; Deadline: Nullify all enemy minions"); //logicalEffect = "nullify all enemy minions"
			s.executeUpdate();
			s.setNull(4, Types.INTEGER); //Doing hexes now
			s.setNull(5, Types.INTEGER);
			s.setString(1, "Overclock");
			s.setInt(3, 2);
			s.setString(6, ":Give +3/+0 to a minion"); //logicalEffect = "give X/X to a minion" 
			s.executeUpdate();
			s.setString(1, "Laser");
			s.setInt(3, 1);
			s.setString(6, ":Deal 2 damage"); //logicalEffect = "deal X damage" 
			s.executeUpdate();
			s.setString(1, "Hack");
			s.setInt(3, 3);
			s.setString(6, ":Force a minion to deal damage to itself"); //logicalEffect = "force a minion to deal damage to itself"
			s.executeUpdate();
			s.setString(1, "Malware");
			s.setInt(3, 6);
			s.setString(6, ":Deal 3 damage to all enemy minions"); //logicalEffect = "deal X damage to all enemy minions"
			s.executeUpdate();
			s.setString(1, "Download");
			s.setInt(3, 6);
			s.setString(6, ":Draw 3 cards"); //logicalEffect = "draw X cards"
			s.executeUpdate();
			s.setString(1, "RAM");
			s.setInt(3, 3);
			s.setString(6, ":Give +3/+3 to a minion"); //logicalEffect = "give X/X to a minion"
			s.executeUpdate();
			s.setString(1, "A* Search");
			s.setInt(3, 4);
			s.setString(6, ":Restore 5 health"); //logicalEffect = "restore X health"
			s.executeUpdate();
			s.setString(1, "Set Task");
			s.setInt(3, 2);
			s.setString(6, ":Give +2/+1 to a minion"); //logicalEffect = "give X/X to a minion"
			s.executeUpdate();
			s.setString(1, "Assist all agents");
			s.setInt(3, 7);
			s.setString(6, ":Give +2/+1 to all minions"); //logicalEffect = "give X/X to all minions"
			s.executeUpdate();
			s.setString(1, "Turing test");
			s.setInt(3, 2);
			s.setString(6, ":Give +0/+3 to a minion"); //logicalEffect = "give X/X to a minion"
			s.executeUpdate();
			s.setString(1, "Autonomize");
			s.setInt(3, 4);
			s.setString(6, ":Restore 8 health to a minion"); //logicalEffect = "restore X health to a minion"
			s.executeUpdate();
			s.setString(1, "Transduce");
			s.setInt(3, 5);
			s.setString(6, ":Draw a card and deal its damage to your target"); //logicalEffect = "draw a card and deal its damage to your target" 
			s.executeUpdate();
			s.setString(1, "Endpoint");
			s.setInt(3, 10);
			s.setString(6, ":Deal 10 damage"); //logicalEffect = "deal X damage" 
			s.executeUpdate();
			s.setString(1, "Delete");
			s.setInt(3, 3);
			s.setString(6, ":Destroy a random enemy minion"); //logicalEffect = "destroy a random enemy minion"
			s.executeUpdate();
			s.setString(1, "Reformat");
			s.setInt(3, 8);
			s.setString(6, ":Deal 7 damage to all minions"); //logicalEffect = "deal X damage to all minions"
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			System.out.println("Failed to add one or more cards");
			e.printStackTrace();
		}
	}

	/**
	 * Fill with player data
	 * @param dbConn database Connection object
	 */
	private static void insertPlayers(Connection dbConn) {
		try {
			PreparedStatement s = 
					dbConn.prepareStatement(
							"INSERT INTO Player VALUES "
						  + "(?, ?, ?, ?, ?, ?, ?, ?)"
					);
			s.setInt(4, 1);
			s.setInt(5, 0);
			s.setInt(6, 0);
			s.setInt(7, 0);
			s.setInt(8, 1);
			String pw;
			String salt;
			for(int i = 1; i <= 5; i++) {
				s.setString(1, "Player" + i);
				pw = "password" + i;
				salt = DBLink.genPWSalt();
				pw = DBLink.hashPW(salt, pw);
				s.setString(2, pw);
				s.setString(3, salt);
				s.executeUpdate();
			}
			s.close();
		} catch (SQLException e) {
			System.out.println("Failed to add one or more players");
			e.printStackTrace();
		}
	}

	/**
	 * Fill with player-owned deck data
	 * @param dbConn databse Connection object
	 */
	private static void insertPlayerDecks(Connection dbConn) {
		try {
			PreparedStatement s = 
					dbConn.prepareStatement(
							"INSERT INTO PlayerDeck (name, owner, collection) VALUES "
						  + "(?, ?, ?)"
					);
			for(int i = 0; i < 10; i++) {
				int n = i / 2 + 1;
				if(i % 2 == 0) {
					s.setString(1, "ProgDeck" + n);
				}
				else {
					s.setString(1, "RobDeck" + n);
				}
				s.setString(2, "Player" + n);
				s.setInt(3, (i % 2) + 2);
				s.executeUpdate();
			}
			s.close();
		} catch (SQLException e) {
			System.out.println("Failed to add one or more PlayerDecks");
			e.printStackTrace();
		}
	}

	/**
	 * Fill with data relating the decks and the cards they contain
	 * @param dbConn database Connection object
	 */
	private static void insertCardDecks(Connection dbConn) {
		try {
			PreparedStatement g =
					dbConn.prepareStatement(
							"SELECT c.cardID FROM Card c WHERE c.cardSet = ?",
							 ResultSet.TYPE_SCROLL_INSENSITIVE,
							 ResultSet.CONCUR_READ_ONLY);
			ResultSet rsProg;
			ResultSet rsRob;
			PreparedStatement s =
					dbConn.prepareStatement(
							"INSERT INTO CardDeck VALUES "
						  + "(?, ?)"
					);
			for(int i = 1; i <= 10; i++) {
				s.setInt(1, i);
				if(i % 2 == 1) {
					g.setInt(1, 2);
					rsProg = g.executeQuery();
					rsProg.absolute(1);
					while(!rsProg.isAfterLast()) {
						s.setInt(2, rsProg.getInt(1));
						s.executeUpdate();
						s.executeUpdate();
						rsProg.next();
					}
				} else {
					g.setInt(1, 3);
					rsRob = g.executeQuery();
					rsRob.absolute(1);
					while(!rsRob.isAfterLast()) {
						s.setInt(2, rsRob.getInt(1));
						s.executeUpdate();
						s.executeUpdate();
						rsRob.next();
					}
				}
			}
			g.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("Failed to add one or more CardDecks");
			e.printStackTrace();
		}
	}
}
