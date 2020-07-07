package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GameData;
import events.EventTrigger;

/**
 * Basic outline for settings page to render the background
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Setting extends JPanel {

	private Tiles tileset;
	private BufferedImage floor, title, home;
	private JLabel returnbutton;
	private AudioSettingsPanel audioset;
	private PlayerSettingsPanel playerset;

	/**
	 * Contructor
	 * @param model Model to change screens
	 * @param aud Audio player to edit audio settings
	 * @param dt Gamedata for player settings
	 * @param trigger EventTrigger for player settings
	 */
	public Setting(GUImodel model, Audio aud, GameData dt, EventTrigger trigger) {
		super();

		tileset = new Tiles();
		floor = tileset.readFloor(0, 1);

		title = tileset.readsmallString("settings", 3);

		home = tileset.readStringwSize("home", 16, 16);
		returnbutton = new JLabel(new ImageIcon(home), JLabel.CENTER);
		returnbutton.addMouseListener(new SettingListener("H", model, returnbutton));
		add(returnbutton);
		
		audioset = new AudioSettingsPanel(aud);
		add(audioset);
		
		playerset = new PlayerSettingsPanel(dt, trigger);
		add(playerset);
	}

	/**
	 * Override paincomponent to render and customise style
	 */
	public void paintComponent(Graphics g) 
	{
		for (int x = 0; x < getWidth(); x += 32) {
			for (int y = 0; y < getHeight(); y += 32) {
				g.drawImage(floor, x, y, null);
			}
		}
		g.drawImage(title, getWidth() / 2 - 160, 20, null);
		returnbutton.setBounds(getWidth() - 180, getHeight() - 30, 200, 30);
		audioset.setBounds(520, 100, 480, 525);
		playerset.setBounds(0, 100, 490, 525);
	}

}
