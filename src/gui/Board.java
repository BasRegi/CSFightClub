package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GameData;

/**
 * Board class to paint cards on panel
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Board extends JPanel {
	private JLabel picback;

	/**
	 * Constructor
	 * @param data GameData to model cards panel
	 */
	public Board(GameData data)
	{
		super();
		setBackground(new Color(0, 0, 0, 123));
	
		picback = new JLabel();
		add(picback);
	}
	
	/**
	 * Override Paint component to animate
	 */
	public void paintComponent(Graphics g)
	{
		picback.setBounds(getWidth() / 20, 4 * getHeight() / 19, 11 * getWidth() / 12, 7* getHeight() / 10);
		super.paintComponent(g);
	}
}


