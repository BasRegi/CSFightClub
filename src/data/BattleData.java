package data;

import java.util.ArrayList;

import events.battle.BattleEffect;

/**
 * A class to store all the values needed to run a battle, especially so that AI and human have
 * a common source of data about the game
 *
 */
public class BattleData {
	public final int handMax = 10;
	public final int maxMana = 10;
	public final int maxMinOnBoard = 6;
	public final int maxHP = 35;

	public Deck myDeck = null, oppoDeck = null;
	public ArrayList<Card> myHand = null, oppoHand = null;
	public boolean myFaceSelectable = false, oppoFaceSelectable = false;

	public ArrayList<Card> myMinions = null, oppoMinions = null;

	public int myHP = 35, oppoHP = 35;
	public int myMana = 0, oppoMana = 0;
	public int turn = 1;
	public int round = 0;
	public ArrayList<BattleEffect> startOfTurn = null;
	public ArrayList<BattleEffect> endOfTurn = null;
	public boolean myTurn = false, oppoTurn = false;
	public boolean battleEnd = false;
	public int myFatigue = 1;
	public int oppoFatigue = 1;

	public boolean myConcede = false, oppoConcede = false;
	public boolean single = true;
	public boolean waitingForTarget = false;
	public int oppoMaxTurnMana = 0;
	public int myMaxTurnMana = 0;
	public String errorMessage = "";
	public boolean oppoTarget = false;
	public boolean myTarget = false;

	/**
	 * Retrieve the deck object storing the user's cards
	 * @return the Deck object containing an ArrayList of Cards
	 */
	public Deck getMyDeck() {
		return myDeck;
	}

	/**
	 * Change the deck that the user is to use
	 * @param newDeck the Deck object with all Cards preloaded
	 */
	public void setMyDeck(Deck newDeck) {
		myDeck = newDeck;
	}

	/**
	 * Retrieve the deck object storing the user's cards
	 * @return the Deck object containing an ArrayList of Cards
	 */
	public Deck getoppoDeck() {
		return oppoDeck;
	}

	/**
	 * Change the deck that the opponent is to use
	 * @param newDeck the Deck object with all Cards preloaded
	 */
	public void setOppoDeck(Deck newDeck) {
		oppoDeck = newDeck;
	}

	/**
	 * Retrieve the subset of the original user Deck that is currently in the user's hand
	 * @return the collection of Cards in an ArrayList
	 */
	public ArrayList<Card> getMyHand() {
		return myHand;
	}

	/**
	 * Retrieve the subset of the original opponent Deck that is currently in the opponent's hand
	 * @return the collection of Cards in an ArrayList
	 */
	public ArrayList<Card> getOppoHand() {
		return oppoHand;
	}

	/**
	 * Retrieve the user's HP in the current game state
	 * @return the user's current HP as an int
	 */
	public int getMyHP() {
		return myHP;
	}

	/**
	 * Change the value of the user's HP
	 * @param newHP the new value that the HP should become
	 */
	public void setMyHP(int newHP) {
		myHP = newHP;
	}

	/**
	 * Retrieve the opponent's HP in the current game state
	 * @return the opponent's current HP as an int
	 */
	public int getOppoHP() {
		return oppoHP;
	}

	/**
	 * Change the value of the opponent's HP
	 * @param newHP the new value that the HP should become
	 */
	public void setOppoHP(int newHP) {
		oppoHP = newHP;
	}

	/**
	 * Retrieve the user's mana in the current game state
	 * @return the user's current mana as an int 
	 */
	public int getMyMana() {
		return myMana;
	}

	/**
	 * Change the amount of mana the user currently has
	 * @param mana the new value that the mana should change to
	 */
	public void setMyMana(int mana) {
		myMana = mana;
	}

	/**
	 * Retrieve the opponent's mana in the current game state
	 * @return the opponent's current mana as an int
	 */
	public int getOppoMana() {
		return oppoMana;
	}

	/**
	 * Change the amount of mana the opponent currently has
	 * @param mana the new value that the mana should change to
	 */
	public void setOppoMana(int mana) {
		oppoMana = mana;
	}
}