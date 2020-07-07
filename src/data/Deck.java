package data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A class to encapsulate a deck into its unique id as stored in the db,
 * and a Collection in the form of an ArrayList to store all the cards
 * @author rxk592
 *
 */
public class Deck {

	private int id;
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	/**
	 * Construct a new deck
	 * @param i the deck's unique id
	 * @param c the ArrayList of Cards
	 */
	public Deck(int i, ArrayList<Card> c) {
		id = i;
		cards = c;
	}
	
	/**
	 * Retrieve the unique id of the deck as it is stored in the database
	 * @return the int value of the unique id
	 */
	public int getDeckID() {
		return id;
	}
	
	/**
	 * Retrieve all the cards in the deck
	 * @return an ArrayList of all cards
	 */
	public ArrayList<Card> getCards() {
		return cards;
	}
	
	/**
	 * Retrieve a single card from the deck from a supplied index
	 * @param index the index in the ArrayList we want to get the card from
	 * @return the Card object in that position
	 */
	public Card getCard(int index) {
		if (index > -1 && index < cards.size()) {
			return cards.get(index);
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Retrieve and permanently remove a card from the deck
	 * @param index the index from which we want to remove the card
	 */
	public void removeCard(int index) {
		if (index > -1 && index < cards.size()) {
			cards.remove(index);
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Completely randomize the order of cards in the deck
	 */
	public void shuffleDeck() {
		Collections.shuffle(cards);
	}

	/**
	 * A wrapper method to retrieve the size of the ArrayList
	 * @return the number of cards in the deck
	 */
	public int size() {
		return cards.size();
	}
}
