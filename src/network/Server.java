package network;

import java.net.*;
import java.io.*;

/**
 * Creates a Server thread where all clients will interface before creating their own dedicated threads
 * @author basilregi
 *
 */
public class Server {

	public static void main(String[] args) {
        
		//Checks the server is initialized with the right amount of arguments
		if(args.length != 1)
		{
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}
		
		//Initializes information required and opens socket
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;
		
		//Opens Socket - with error handling
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't listen on port: " + port);
			System.exit(1);
		}
		
		PlayerTable playersTable = new PlayerTable();
		MatchPool pool = new MatchPool(playersTable);
		pool.start();
		
		//Starts the server loop looking for clients to connect and error handling each time
		try
		{
			while(true)
			{
				if(playersTable.size() < 20)
				{
					//Listens to socket looking for new players
					Socket socket = serverSocket.accept();
					
					
					//So we can take name input from player
					PrintStream toPlayer = new PrintStream(socket.getOutputStream());
					BufferedReader fromPlayer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
					
					//Asks for playerid
					String msg = null;
					msg = fromPlayer.readLine();
					Command cmd = Protocol.decode(msg);
					String playerid = cmd.getSender();
					
					//Shows when player has connected
					System.out.println(playerid + " has connected");
					
					//Creates a new player in the table
					playersTable.add(playerid);
					
					//Starts a player specific reciever
					(new ServerReciever(playerid, fromPlayer, playersTable, pool)).start();
					
					//Starts a player specific sender
					(new ServerSender(playersTable.getQueue(playerid), toPlayer)).start();
				}
				
			}
		}
		
		catch (IOException e)
		{
			//System.err.println("IO Error: " + e.getMessage());
		}

	}
}