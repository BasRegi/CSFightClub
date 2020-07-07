package network;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is a class to hold a list of players and the messqge queue associated to them
 * @author basilregi
 *
 */
public class PlayerTable {
	
	private int size = 0;
	private ConcurrentMap<String, MessageQueue> playerTable = new ConcurrentHashMap<String, MessageQueue>();

	/**
	 * Returns the number of people connected
	 * @return size of table
	 */
	public int size() 
	{
		return size;
	}

	/**
	 * Adds a player to the playerTable
	 * @param playerid ID of player to be added
	 */
	public void add(String playerid) 
	{
		playerTable.put(playerid, new MessageQueue());
		size++;
	}
	
	/**
	 * Remove a player from the player table
	 * @param playerid ID of player to be removed
	 */
	public void remove(String playerid)
	{
		playerTable.remove(playerid);
		size--;
	}
	
	/**
	 * Get MessageQueue for specific player
	 * @param playerid ID of player to get Queue for
	 * @return BlockingQueue of messages for player
	 */
	public MessageQueue getQueue(String playerid)
	{ 
		return playerTable.get(playerid);
	}
	
	/**
	 * Return the list of players currently connected
	 * @return list of player IDs
	 */
	public Set<String> getKeySet()
	{
		return playerTable.keySet();
	}

}
