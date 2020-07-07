package data;

/**
 * A class to encapsulate a user's account details
 * @author rxk592
 *
 */
public class Charactor {
	private String name = "new Player";
	private Integer year = 1 ;
	private Integer credits = 0;
	private Integer played = 0;
	private Integer won = 0;
	private Integer picID = 1;
	
	/**
	 * Get the user's unique username
	 * @return the username
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Change the user's name
	 * @param name the new username
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the 'Year' the user is in - game level
	 * @return the integer value of the year
	 */
	public Integer getYear() {
		return year;
	}
	
	/**
	 * Change the user's year
	 * @param yr the new year the user is in
	 */
	public void setYear(Integer yr) {
		this.year = yr;
	}
	
	/**
	 * Get the number of 'credits' the user has - game xp
	 * @return the integer value of the credits
	 */
	public Integer getCredits() {
		return credits;
	}
	
	/**
	 * Change the number of credits a user has
	 * @param credits the new number of credits
	 */
	public void setCredits(Integer credits) {
		this.credits = credits;
	}
	
	/**
	 * Get the number of games the user has played
	 * @return the integer value of the number of games
	 */
	public Integer getPlayed() {
		return played;
	}
	
	/**
	 * Change the number of games a user has played
	 * @param p the new number of games
	 */
	public void setPlayed(int p) {
		this.played = p;
	}
	
	/**
	 * Get the number of games a user has won
	 * @return the integer number of games won
	 */
	public Integer getWon() {
		return won;
	}
	
	/**
	 * Set the number of games a user has won
	 * @param w the new number of games won by the user
	 */
	public void setWon(int w) {
		this.won = w;
	}
	
	/**
	 * when a user has played a game, increase the relevant fields
	 * @param win true if the user won the game just played, false otherwise
	 * @return true if the player has levelled up, false otherwise
	 */
	public boolean oneMoreGame(boolean win){
		played++;
		if (win) {
			System.out.println("I WON");
			won++;
			credits = credits + 10;
		} else {
			credits = credits + 4;
		}
		if(credits >= 120) {
			credits = 0;
			year++;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * get the ID of the picture associated with the user's account
	 * @return the integer value of the picture id
	 */
	public Integer getPicID() {
		return picID;
	}
	
	/**
	 * set a new pic id to associate with the user's account
	 * @param picID the new ID
	 */
	public void setPicID(Integer picID) {
		this.picID = picID;
	}
	
	public String toString() {
		return (name+": "+year+","+credits+","+played+","+won+","+picID);
	}
}
