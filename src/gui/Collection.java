package gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GameData;

/**
 * Class to show collections of deck
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Collection extends JPanel {

	private Tiles tileset;
	private BufferedImage floor, title, home;
	private JLabel returnbutton;
	private JButton card, card1;

	/**
	 * Constructor
	 * @param model Model to change screen
	 * @param gameData GameData to get card data
	 */
	public Collection(GUImodel model, GameData gameData) {
		super();

		tileset = new Tiles();
		floor = tileset.readFloor(3, 0);

		title = tileset.readsmallString("collection", 3);

		home = tileset.readStringwSize("home", 16, 16);
		returnbutton = new JLabel(new ImageIcon(home), JLabel.CENTER);
		returnbutton.addMouseListener(new CollectionListener("H", model, returnbutton));
		add(returnbutton);

		card = new JButton("Programming");
		card.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.collectStart = 0;
				gameData.collectEnd = 28;
				model.changeScreen("P");
			}

		});
		add(card);
		card1 = new JButton("Robotics");
		card1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.collectStart = 29;
				gameData.collectEnd = 57;
				model.changeScreen("P");
			}

		});
		add(card1);
	}


	/**
	 * Override paintcomponent to animate
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int x = 0; x < getWidth(); x += 32) {
			for (int y = 0; y < getHeight(); y += 32) {
				g.drawImage(floor, x, y, null);
			}
		}
		g.drawImage(title, getWidth() / 2 - 250, 20, null);
		returnbutton.setBounds(getWidth() - 180, getHeight() - 30, 200, 30);
		card.setBounds(4 * getWidth() / 19, 5 * getHeight() / 20, 200, 350);
		card1.setBounds(4 * getWidth() / 7, 5 * getHeight() / 20, 200, 350);
	}
}