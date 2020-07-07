package network;
/**
 * Protocol class to encode the Commands as string to send them over the server
 * @author basilregi
 *
 */
//NEEDS IMPROVEMENT. FIND A GOOD WAY TO ENCODE MESSAGES AS STRINGS.
public class Protocol {

	/**
	 * Static encode method to encode Command as String
	 * @param input Command to encode
	 * @return String of command
	 */
	public static Command decode(String input) 
	{
		String[] data = new String[4];
		data = input.split(";");
		Command cmd = new Command(data[0], data[1], data[2], data[3]);
		return cmd;
	}
	
	/**
	 * Static decode method to decode Command type from string
	 * @param msg String to decode
	 * @return Command
	 */
	public static String encode(Command msg)
	{
		String type = msg.getType();
		String sender = msg.getSender();
		String recipient = msg.getReciever()!=null ? msg.getReciever() : " ";
		String message = msg.getMessage()!=null? msg.getMessage() : " ";
		System.out.println(type + ";" + sender + ";" + recipient + ";" + message);
		return (type + ";" + sender + ";" + recipient + ";" + message);
	}

}
