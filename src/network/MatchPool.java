package network;

import java.util.ArrayList;
import java.util.Random;
/**
 * This class will be used to create pairings for a battle, it adds players to an arraylist when they request for battle
 * and then choses two of them to paired together, assigns them 1 or 2 according to what number player they are
 * and then sends them a message giving this information, that they've been matched and then removes them from the arraylist.
 * 
 * Constantly running on an individual thread.
 * @author basilregi
 *
 */
public class MatchPool extends Thread 
{
	private ArrayList<String> players;
	private PlayerTable playerTable;
	
	/**
	 * Constructed with a player table to send them messages when they have been connected.
	 * @param playerTable - PlayerTable object
	 */
	public MatchPool(PlayerTable playerTable)
	{
		this.playerTable = playerTable;
		players = new ArrayList<String>();
	}
	
	/**
	 * Adds player to the arralist to indicate they have requested for battle
	 * @param playerid
	 */
	public synchronized void add(String playerid)
	{
		players.add(playerid);
	}
	
	/**
	 * private method to randomly select two people from the arraylist and match them together, sends them both a message
	 * saying theyve been matched and then removes them from the list.
	 */
	private synchronized void random_match()
	{
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random r = new Random();
		int p1 = r.nextInt(players.size());
		int p2 = r.nextInt(players.size());
		String p1n = players.get(p1);
		String p2n = players.get(p2);
		
		System.out.println(p1n + "," + p2n);
		while(p1n.equals(p2n))
		{
			p2 = r.nextInt(players.size());
			p2n = players.get(p2);
		}
		
		MessageQueue p1q = playerTable.getQueue(players.get(p1));
		MessageQueue p2q = playerTable.getQueue(players.get(p2));
		
		long deckSeed1 = r.nextLong();
		long deckSeed2 = r.nextLong();
		
		Command cmd1 = new Command("MD", p2n, p1n, "First:"+deckSeed1+":"+deckSeed2);
		Command cmd2 = new Command("MD", p1n, p2n, "Second:"+deckSeed2+":"+deckSeed1);
		
		players.remove(p1n);
		players.remove(p2n);
		
		p1q.offer(cmd1);
		p2q.offer(cmd2);
		
	}
	
	/**
	 * This method will start the thread and constantly try to assign matches between the players in the arraylist.
	 * If there is only one player it will send them a message saying they are the only player requesting for battle
	 */
	public void run()
	{
		while(true)
		{
			if(players.size() == 0)
			{
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			else if(players.size() == 1)
			{
				String onlyPlayer = players.get(0);
				Command cmd = new Command("MW", "Server", onlyPlayer, "No players. Waiting for players to connect ..." + onlyPlayer);
				MessageQueue playerq = playerTable.getQueue(onlyPlayer);
				try 
				{
					playerq.offer(cmd);
					Thread.sleep(500);
				} 
				catch (InterruptedException e) 
				{
					//players.remove(on);
					//e.printStackTrace();
				}
			}
			else
			{
				random_match();
			}
		}
	}
	

}
