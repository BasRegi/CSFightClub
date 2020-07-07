package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class to listen to keyboard inputs in lobby and move character
 * @author basilregi
 *
 */
public class MoveAction extends Thread
{
	private int key;
	private boolean isPressed;
	private JLabel character;
	private JPanel panel;
	
	/**
	 * Constructor
	 * @param character JLabel containing character
	 * @param panel JPanel that the character is on
	 */
	public MoveAction(JLabel character, JPanel panel)
	{
		key = 0;
		isPressed = false;
		this.character = character;
		this.panel = panel;
	}
	
	/**
	 * Seperate thread to constantly listen for inputs and move character accordingly without delay
	 */
	public void run()
	{
		while(true)
		{
			if(getIsPressed())
			{
				int x = character.getX();
				int y = character.getY();
				if((detectColl(x, y) == 0))
				{
					switch (getKey()) {
						case 1:
							if(y <= 3)
								break;
							character.setBounds(x, y - 5, 32, 50);
							panel.repaint();
							break;
						case 2:
							if(y >= 630)
								break;
							character.setBounds(x, y + 5, 32, 50);
							panel.repaint();
							break;
						case 3:
							if(x <= 3)
								break;
							character.setBounds(x - 5, y, 32, 50);
							panel.repaint();
							break;
						case 4:
							if(x >= 968)
								break;
							character.setBounds(x + 5, y, 32, 50);
							panel.repaint();
							break;
						case 0:
							break;
					}
				}
				else
				{
					switch (detectColl(x, y)) {
					case 1:
						character.setBounds(x, y + 5, 32, 50);
						panel.repaint();
						break;
					case 2:
						character.setBounds(x, y - 100, 32, 50);
						panel.repaint();
						break;
					case 3:
						character.setBounds(x + 5, y, 32, 50);
						panel.repaint();
						break;
					case 4:
						character.setBounds(x - 5, y, 32, 50);
						panel.repaint();
						break;
					}
				}
			}
			try 
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Move action is for specific key so set the key to listen for
	 * @param key Key to be set
	 */
	public synchronized void setKey(int key)
	{
		this.key = key;
	}
	
	/**
	 * Gets the current key its listening on
	 * @return the int value of current key
	 */
	public synchronized int getKey()
	{
		return this.key;
	}
	
	/**
	 * Boolean value to let the listener know that key is pressed
	 * @param t boolean value
	 */
	public synchronized void setIsPressed(boolean t)
	{
		isPressed = t;
	}
	
	/**
	 * Gets whether the key is pressed
	 * @return
	 */
	public synchronized boolean getIsPressed()
	{
		return this.isPressed;
	}
	
	/**
	 * Detects a collision with the edge of the frame given x and y
	 * @param x x coordinate fo character
	 * @param y y coordinate of character
	 * @return value to indicate where collision has taken place
	 */
	public int detectColl(int x, int y)
	{
		if(x > 128 && x < 850 && y > 200 && y < 550)
		{
			
		}
		else if(x <= 128)
		{
			if(x <= 3) {return 3;}
		}
		else if (x >= 750)
		{
			if(x >= 950) {return 4;}
		}
		else if(y <= 250)
		{
			if(y <= 3) {return 1;}
		}
		else if(y >= 550)
		{
			if(y >= 800) {return 2;}
		}
		return 0;
	}
}
