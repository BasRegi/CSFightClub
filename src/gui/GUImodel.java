package gui;

import java.util.Observable;

/**
 * Model class to be observed by all of the GUI to intiate screen and state changes within screen
 * @author basilregi
 *
 */
public class GUImodel extends Observable {
	private String screen;
	private String state;
	public boolean tutorialviewed = false;

	/**
	 * Constructor
	 */
	public GUImodel() {
		screen = "W";
		state = "";
	}
	
	/**
	 * Change state without notifying observers
	 * @param newstate new state of model
	 */
	public void changeStateQuietly(String newstate){
		state = newstate;
	}

	/**
	 * Gets the value of Screen
	 * @return String current screen
	 */
	public String getScreen() {
		return screen;
	}

	/**
	 * Gets the current state
	 * @return String current state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Change screen field and notify observers
	 * @param newscreen New screen to change to
	 */
	public void changeScreen(String newscreen) {
		screen = newscreen;
		setChanged();
		notifyObservers();
	}

	/**
	 * Change state and notify observers
	 * @param newstate new state to change to
	 */
	public void changeState(String newstate) {
		state = newstate;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Change both at the same time and notify observers after
	 * @param newScreen
	 * @param newState
	 */
	public void changeBoth(String newScreen, String newState)
	{
		state = newState;
		screen = newScreen;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Change state and notify observers
	 * @param newstate
	 */
	public void setState(String newstate) {
		state = newstate;
	}
}
