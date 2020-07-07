package network;

import java.io.PrintStream;

/**
 * Specific sender thread for server, everytime there is a message in the queue, server will send it through the output stream
 * @author basilregi
 *
 */
public class ServerSender extends Thread 
{
	private MessageQueue messagesForPlayer;
	private PrintStream toPlayer;
	
	/**
	 * Constructor with message queue for specific client, and print stream
	 * @param messagesForPlayer
	 * @param toPlayer
	 */
	public ServerSender(MessageQueue messagesForPlayer, PrintStream toPlayer)
	{
		this.messagesForPlayer = messagesForPlayer;
		this.toPlayer = toPlayer;
	}
	
	/**
	 * Constantly runs to see if there is a message in queue and then sends it if there is one. 
	 */
	public void run()
	{
		while(true)
		{
			Command msg = messagesForPlayer.take();
			toPlayer.println(Protocol.encode(msg));
		}
	}

}
