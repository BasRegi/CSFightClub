package data;

import java.util.ArrayList;

import network.Player;

/**
 * A class to store the necessary data to run a lobby
 * 
 */
public class LobbyData {
	public Player client;
	public String receiver;
	public ArrayList<String> messageHistory = new ArrayList<String>();
	
	public boolean messageSent = false;
}
