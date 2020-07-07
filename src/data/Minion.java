package data;

/**
 * A class to store all information about a minion together, as only minions have attack/defence.
 * Before getting/setting attack/defence or isDestroyed, ALWAYS ensure value of isNull first
 * @author rxk592
 *
 */
public class Minion { 
	
	private boolean nullminion;
	private int attack, maxAttack;
	private int defence, maxDefence;
	public boolean selectable = false;
	public String name;
	
	/**
	 * Empty constructor sets minion to a safe null (i.e. the Card is a hex)
	 */
	public Minion(){
		nullminion = true;
		name = "";
		attack = -1;
		defence = -1;
	}
	
	/**
	 * Constructor to set values for attack and defence
	 * @param a the attack value of the minion
	 * @param d the defence value of the minion
	 */
	public Minion(String name, int a, int d) {
		nullminion = false;
		attack = a;
		setMaxAttack(a);
		defence = d;
		setMaxDefence(d);
	}
	
	/**
	 * Gives whether the card is a minion card or not
	 * @return true if the card is a hex, false if the card is a minion
	 */
	public boolean isNull() {
		return nullminion;
	}
	
	/**
	 * Change the attack value of a minion card
	 * @param a the new attack value
	 */
	public void setAttack(int a) {
		attack = a;
	}
	
	/**
	 * Change the defence value of a minion card
	 * @param d the new defence value
	 */
	public void setDefence(int d) {
		defence = d;
	}
	
	/**
	 * Get the current attack value of a minion card
	 * @return the attack value
	 */
	public int getAttack() {
		return attack;
	}
	
	/**
	 * Get the current defence value of a minion card
	 * @return the defence value
	 */
	public int getDefence() {
		return defence;
	}
	
	/**
	 * Determine whether the minion has been destroyed
	 * @return true if the minion's health value has gone below 0, false otherwise
	 */
	public boolean isDestroyed() {
		return (defence <= 0);
	}
	
	public String toString(){
		return ("Name = " + name + "Attack = " + getAttack() + "; Defence = " + getDefence());
	}

	/**
	 * Retrieve the maximum attack value of the Minion
	 * @return the maximum attack as an int
	 */
	public int getMaxAttack() {
		return maxAttack;
	}

	/**
	 * Set the maximum attack of the Minion to a new level
	 * @param maxAttack the new maximum attack level
	 */
	public void setMaxAttack(int maxAttack) {
		this.maxAttack = maxAttack;
	}

	/**
	 * Retrieve the maximum defence value of the Minion
	 * @return the maximum defence as an int
	 */
	public int getMaxDefence() {
		return maxDefence;
	}

	/**
	 * Set the maximum defence of a Minion to a new level
	 * @param maxDefence the new maximum defence level
	 */
	public void setMaxDefence(int maxDefence) {
		this.maxDefence = maxDefence;
	}
}

