package gui;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

/**
 * Listener for the settings page
 * @author basilregi
 *
 */
public class SettingListener implements MouseListener {
	private String type;
	private GUImodel model;
	private JLabel button;
	private Tiles tileset = new Tiles();
	BufferedImage img;

	/**
	 * Constructor
	 * @param type Type of button
	 * @param model model to change screen
	 * @param button Button object to edit style
	 */
	public SettingListener(String type, GUImodel model, JLabel button) {
		this.type = type;
		this.model = model;
		this.button = button;
	}

	/**
	 * Action when mouse clicks on button
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (type.equals("H"))

		{
			model.changeScreen("H");

		}
	}

	/**
	 * Action when mouse presses button
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}

	/**
	 * Action when mouse is released after pressing button
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		button.setBorder(BorderFactory.createEmptyBorder());
		mouseClicked(e);
	
	}

	/**
	 * Action when mouse hovers over button
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(type.equals("H"))
		{
			img = tileset.readStringwSize("home", 20, 20);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		}
	}

	/**
	 * Action when mouse hovers out of button
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		if(type.equals("H"))
		{
			img = tileset.readStringwSize("home", 16, 16);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		}
	}
}