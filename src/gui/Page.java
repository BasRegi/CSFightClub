package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GameData;

@SuppressWarnings("serial")
public class Page extends JPanel{
	private Tiles tileset;
	private BufferedImage floor, title, home;
	private JLabel returnbutton;
	private Deck deck;
	
	public Page(GUImodel model, GameData gameData) {
		super();

		tileset = new Tiles();
		floor = tileset.readFloor(3, 0);

		title = tileset.readsmallString("deck", 3);

		home = tileset.readStringwSize("collection", 16, 16);
		returnbutton = new JLabel(new ImageIcon(home), JLabel.CENTER);
		returnbutton.addMouseListener(new PageListener("CO", model, returnbutton));
		add(returnbutton);
		
		deck = new Deck(gameData.collectStart, gameData.collectEnd, gameData);
		add(deck);
		
	}

public void paintComponent(Graphics g) {
	super.paintComponent(g);

	for (int x = 0; x < getWidth(); x += 32) {
		for (int y = 0; y < getHeight(); y += 32) {
			g.drawImage(floor, x, y, null);
		}
	}
	g.drawImage(title, getWidth() / 2 - 100, 20, null);
	//doDrawing(g);
	returnbutton.setBounds(getWidth() - 290, getHeight() - 30, 350, 30);
	deck.setBounds(getWidth() / 20, 4 * getHeight() / 19, 11 * getWidth() / 12, 7* getHeight() / 10);
}}
