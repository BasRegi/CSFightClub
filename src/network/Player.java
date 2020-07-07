package network;
import data.*;
import events.*;

import java.io.*;
import java.net.*;

/**
 * Client class, this has to be initialised in the event manager and has to be connected for all other methods to work
 * @author basilregi
 *
 */
public class Player {
	
	private EventTrigger trigger;
	private PrintStream toServer = null;
	private BufferedReader fromServer = null;
	private Socket server = null;
	private PlayerReciever reciever;
	public String playerid;
	private String hostname;
	private int port;

	/**
	 * Contruct with game data manager, event manager, a port and host to connect to
	 * @param data - GameData
	 * @param trigger - EventTrigger
	 * @param port_s
	 * @param hostname
	 */
	public Player(GameData data, EventTrigger trigger, String port_s, String hostname) {
		
		//Initialize information
		this.trigger = trigger;
		playerid = data.getCharactor().getName();
		this.port = Integer.parseInt(port_s);
		this.hostname = hostname;
		
	}
	
	/**
	 * This method has to be called before sending any messages to the server as the connection has to be set up
	 * @return whether it has connected sucessfully
	 */
	public boolean connect()
	{
		try 
		{
			System.out.println("Trying to connect");
			server = new Socket(hostname, port);
			System.out.println("Server Connected");
			toServer = new PrintStream(server.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
		}
		catch (UnknownHostException e)
		{
			System.err.println("Unknown Host: " + hostname);
			return false;
		}
		catch (IOException e)
		{
			System.err.println("The server doesn't seem to be running " + e.getMessage());
			return false;
		}
		
		if(toServer != null && fromServer != null)
		{
			Command fstmsg = new Command("M", playerid, "Server", "hi");
			toServer.println(Protocol.encode(fstmsg));
			
			reciever = new PlayerReciever(fromServer, trigger);
			
			reciever.start();
			return true;
		}
		return false;
	}
	
	/**
	 * Close the connection by closing the input and output streams
	 */
	public void close()
	{
		try
		{
			reciever.interrupt();
			toServer.close();
			fromServer.close();
			server.close();
		}
		catch(IOException e)
		{
			System.err.println("IOError Client");
			System.exit(1);
		}
	}
	
	/**
	 * Sends a Command across the server, after encoding it as a string
	 * @param cmd - Command to send across the server
	 */
	public void sendMessage(Command cmd)
	{
		String encmsg = Protocol.encode(cmd);
		toServer.println(encmsg);
	}

}
