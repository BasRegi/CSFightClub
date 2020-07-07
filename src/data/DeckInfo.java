package data;

/**
 * A class to store information about a deck without having to know all the cards it stores
 * as that can be queried from the database only when needed
 * @author rxk592
 *
 */
public class DeckInfo {
	
	private int id;
	private String name;
	private String collection;
	private String owner;

	/**
	 * Construct a new object
	 * @param i the unique id of the deck in the database
	 * @param n the name given to the deck
	 * @param o the unique username describing the owner of the deck
	 * @param c the set that the cards in the deck are from (Programming / Robotics)
	 */
	public DeckInfo(int i, String n, String o, String c) {
		id = i;
		name = n;
		collection = c;
		owner = o;
	}
	
	/**
	 * Retrieve the unique id of the deck as it is stored in the database
	 * @return the int value of the unique id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieve the name given to the deck
	 * @return the name of the deck as a String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieve the set that the cards in the deck belong to
	 * @return the name of the set as a String
	 */
	public String getCollection() {
		return collection;
	}
	
	/**
	 * Retrieve the unique username of the owner of the deck
	 * @return the username of the Player as a String
	 */
	public String getOwner() {
		return owner; 
	}
	
	@Override
	public boolean equals(Object di) {
		DeckInfo d = (DeckInfo) di;
		return (id == d.getID()) && (name.equals(d.getName()) &&
			   (owner.equals(d.getOwner())) && collection.equals(d.getCollection()));
	}
	
	public String toString() {
		return "(" + id + ", " + name + ", " + owner + ", " + collection + ")";
	}
}
