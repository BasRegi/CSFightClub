package network;
import events.EventTrigger;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * This is a separate thread to constantly listen on the input stream from the server to deal with messages coming in
 * @author basilregi
 *
 */
public class PlayerReciever extends Thread 
{
	private BufferedReader fromServer;
	private EventTrigger trigger;
	/**
	 * Constructor with inputstream and trigger to send messages to
	 * @param fromServer - BufferedReader input
	 * @param trigger - EventTrigger
	 */
	public PlayerReciever(BufferedReader fromServer, EventTrigger trigger) 
	{
		this.fromServer = fromServer;
		this.trigger = trigger;
	}
	
	/**
	 * Thread runs until inputstream becomes null, otherwise passes messages onto eventrigger to deal with it
	 */
	public void run()
	{
		while(true)
		{
			try 
			{
				if(fromServer == null)
				{
					this.join();
				}
				String msg = fromServer.readLine();
				if(msg != null)
				{
					Command cmd = Protocol.decode(msg);
					if(cmd.getType().equals("M"))
					{
						trigger.netInput("receiveMessage", cmd.getSender(), cmd.getMessage());
					}
					else if(cmd.getType().equals("MD"))
					{
						String[] msgs = cmd.getMessage().split(":");
						trigger.netInput("matched", cmd.getSender(), msgs[0], msgs[1], msgs[2]);
					}
					else if(cmd.getType().equals("MW"))
					{
						trigger.netInput("receiveMessage", "SYSTEM", cmd.getMessage());
					}
					else if(cmd.getType().equals("A"))
					{
						trigger.netInput("receiveMessage", cmd.getSender(), cmd.getMessage());
					}
					else if(cmd.getType().equals("BM"))
					{
						String[] batMsg = cmd.getMessage().split(":");
						if (batMsg.length==1) {
							trigger.netInput(batMsg[0]);
						} else {
							trigger.netInput(batMsg[0], batMsg[1]);
						}
						
					}
				}
			} 
			catch (IOException e) 
			{
				//Server broke
			} 
			catch (InterruptedException e) 
			{
				//Server broke
			}
			
		}
	}
}