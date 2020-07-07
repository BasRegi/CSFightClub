package network;

import java.io.*;

/**
 *A separate thread to deal with messages being recieved by a specific client
 * @author basilregi
 *
 */
public class ServerReciever extends Thread 
{
	private String playerid;
	private BufferedReader fromPlayer;
	private PlayerTable playerTable;
	private MatchPool pool;
	
	/**
	 * Contructor with the clients id, output stream, player table and the matchpool thread
	 * @param playerid - String 
	 * @param fromPlayer - Input
	 * @param playerTable - Table to put messages into
	 * @param pool - Pool to match players
	 */
	public ServerReciever(String playerid, BufferedReader fromPlayer, PlayerTable playerTable, MatchPool pool)
	{
		this.playerid = playerid;
		this.fromPlayer = fromPlayer;
		this.playerTable = playerTable;
		this.pool = pool;
	}
	
	/**
	 *Runs until input stream is null at which point player is disconnected, until then whenever the server recieves 
	 *a message it puts in the appropriate queue for the player.
	 */
	public void run()
	{
		while(true)
		{
			try 
			{
				String input = fromPlayer.readLine();
				if(input == null)
				{
					System.out.println(playerid + " Disconnected");
					playerTable.remove(playerid);
					this.join();
				}
				if(input != "")
				{
					Command cmd = Protocol.decode(input);
					String type = cmd.getType();
					if (type.equals("M"))
					{
						MessageQueue queue = playerTable.getQueue(cmd.getReciever());
						if(queue != null)
						{
							queue.offer(cmd);
						}
					}
					else if(type.equals("MR"))
					{
						System.out.println(cmd.getSender() + " has requested for battle");
						pool.add(cmd.getSender());
					}
					else if(type.equals("A"))
					{
						System.out.println("A type");
						for(String key: playerTable.getKeySet())
						{
							System.out.println(key);
							MessageQueue q = playerTable.getQueue(key);
							q.offer(cmd);
						}
					}
					else if(type.equals("BM")){
						System.out.println("Battle Message between "+cmd.getSender()+" and "+cmd.getReciever());
						MessageQueue queue = playerTable.getQueue(cmd.getReciever());
						if(queue != null)
						{
							queue.offer(cmd);
						}
					}
				}

			} 
			catch (IOException e) 
			{
				playerTable.remove(playerid);
				try 
				{
					fromPlayer.close();
					this.join();
				} 
				catch (InterruptedException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
