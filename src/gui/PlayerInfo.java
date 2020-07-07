package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Charactor;
import data.GameData;

/**
 * Info panel to show stats of play on home screen
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class PlayerInfo extends JPanel
{
	private Charactor c;
	private JLabel pic;
	private JPanel picback;
	private JLabel infolabel1;
	private BufferedImage sidefloor, title;
	
	/**
	 * Constructor
	 * @param dt GameData to get player stats
	 */
	public PlayerInfo(GameData dt)
	{
		super();
		setBackground(new Color(0, 0, 0, 123));
		
		c = dt.getCharactor();
		
		Tiles tileset = new Tiles();
		
		sidefloor = tileset.readFloor(0, 0);
		
		title = tileset.readStringwSize("player info", 40, 40);
		
		BufferedImage p1 = tileset.readCharacter(c.getPicID(), 0, 0, 150, 300);
		pic = new JLabel(new ImageIcon(p1), JLabel.CENTER);
		pic.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(pic);
		
		
		String info1 = "<html><table<tr><td>Name: </td><td>" + c.getName() + "</td></tr>"
						+ "<tr><td>Year: </td><td>" + c.getYear() + "</td></tr>"
						+ "<tr><td>Credits: </td><td>" + c.getCredits() + "</td></tr>"
						+ "<tr><td>Games Won: </td><td>" + c.getWon() + "</td></tr>"
						+ "<tr><td>Games Played: </td><td>" + c.getPlayed() + "</td></tr>"
						+ "</table></html>";
		infolabel1 = new JLabel(info1);
		Font infofont = new Font("Academy Engraved LET", Font.PLAIN, 32);
		infolabel1.setFont(infofont);
		infolabel1.setForeground(Color.WHITE);
		add(infolabel1);
		
		picback = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				for(int x = 0; x < picback.getWidth(); x += 32)
				{
					for(int y = 0; y < picback.getHeight(); y += 32)
					{
						g.drawImage(sidefloor, x, y, null);
					}
				}
			}
		};
		picback.setBounds(200, 50, 250, 250);
		add(picback);
	}
	
	/**
	 * Override paint component to cutsomise style
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(title, 10, 10, null);
		picback.setBounds(20, 60, 250, 400);
		pic.setBounds(20, 60, 250, 400);
		infolabel1.setBounds(280, 40, 300, 400);
	}
}
