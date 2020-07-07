package network;
/**
 * This class is template to store messages to be sent over the server, it has 4 paramters:
 * - type
 * - sender
 * - reciever
 * - and a message which may contain more paramters.
 * @author basilregi
 *
 */
public class Command 
{
	private String sender;
	private String reciever;
	private String message;
	private String type;
	
	/**
	 * Construct with type, sender, reciever and a message
	 * @param type - String type
	 * @param sender - ID of sender
	 * @param reciever - ID of reciever
	 * @param message - String message to send
	 */
	public Command(String type, String sender, String reciever, String message)
	{
		this.type = type;
		this.sender = sender;
		this.reciever = reciever;
		this.message = message;
	}

	/**
	 * Returns type of command
	 * @return String of type
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Returns the playerid of sender
	 * @return String sender ID
	 */
	public String getSender()
	{
		return sender;
	}
	
	/**
	 * Returns the playerid of reciever
	 * @return String reciever ID
	 */
	public String getReciever()
	{
		return reciever;
	}
	
	/**
	 * Returns the message
	 * @return String message
	 */
	public String getMessage()
	{
		return message;
	}
	

}
