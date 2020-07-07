package network;

import java.util.concurrent.*;

/**
 * This is a queue wrapper class to wrap the queue assigned to each player once they connect to the server
 * Each queue holds command type objects in a blocking queue.
 * @author basilregi
 *
 */
public class MessageQueue {

	private BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();
	
	/**
	 * Puts a message in the queue
	 * @param m Command to put in the queue
	 */
	public void offer(Command m)
	{
		queue.offer(m);
	}
	
	/**
	 * Takes a message from the queue
	 * @return a Command
	 */
	public Command take()
	{
		while(true)
		{
			try
			{
				return(queue.take());
			}
			catch(InterruptedException e)				
			{
				//No interrupts used
			}
		}
	}
}