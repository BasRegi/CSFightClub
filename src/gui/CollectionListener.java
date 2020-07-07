package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

/**
 * Listener for the collection page
 * @author basilregi
 *
 */
public class CollectionListener implements MouseListener {
	private String type;
	private GUImodel model;
	private JLabel button;
	private Tiles tileset = new Tiles();
	BufferedImage img;

	/**
	 * Constructor
	 * @param type Type of button
	 * @param model Model to change screen
	 * @param button Button to update animation
	 */
	public CollectionListener(String type, GUImodel model, JLabel button) {
		this.type = type;
		this.model = model;
		this.button = button;
	}

	/**
	 * Action when mouse clicks button
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (type.equals("H"))

		{
			model.changeScreen("H");

		}
	}

	/**
	 *Animation when mouse is pressed down on button
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}

	/**
	 * Animation when mouse is released after being pressed down
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		button.setBorder(BorderFactory.createEmptyBorder());
		mouseClicked(e);
	
	}

	/**
	 * Animation when mouse hovers over button
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
	 * Animation when mouse exits the button
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
