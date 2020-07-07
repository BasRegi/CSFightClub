package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import events.EventTrigger;

/**
 * Listener class for button objects in Lobby page
 * @author basilregi
 *
 */
public class LobbyListener implements MouseListener {
	private String type;
	private GUImodel model;
	private JLabel button;
	private Tiles tileset = new Tiles();
	private BufferedImage img;
	private EventTrigger trigger;
    
	/**
	 * Constructor
	 * @param type Type of button
	 * @param model - Model to change screens
	 * @param button - Button object to edit style
	 * @param trigger - Eventtrigger to cause changes
	 */
	public LobbyListener(String type, GUImodel model, JLabel button, EventTrigger trigger) {
		this.type = type;
		this.model = model;
		this.button = button;
		this.trigger = trigger;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (type.equals("LO")) {
			trigger.myInput("back", "");
			model.changeScreen("H");
		} else if (type.equals("B")) {
			trigger.myInput("match");
		} else if (type.equals("T")) {
			model.changeBoth("T", model.getScreen());
		} else if (type.equals("Slo")) {
			JOptionPane.showMessageDialog(null, "Not yet implemented", "FUTURE DLC", JOptionPane.PLAIN_MESSAGE, null);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		button.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (type.equals("LO")) {
			img = tileset.readStringwSize("home", 20, 20);
			button.setIcon(new ImageIcon(img));
			button.repaint();

		} else if (type.equals("B")) {
			img = tileset.readStringwSize("match", 20, 20);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		} else if (type.equals("T")) {
			img = tileset.readStringwSize("help", 20, 20);
			button.setIcon(new ImageIcon(img));
			button.repaint();

		} else if (type.equals("Slo")) {
			img = tileset.readStringwSize("sloman store", 20, 20);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (type.equals("LO")) {
			img = tileset.readStringwSize("home", 16, 16);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		} else if (type.equals("B")) {
			img = tileset.readStringwSize("match", 16, 16);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		} else if (type.equals("T")) {
			img = tileset.readStringwSize("help", 16, 16);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		} else if (type.equals("Slo")) {
			img = tileset.readStringwSize("sloman store", 16, 16);
			button.setIcon(new ImageIcon(img));
			button.repaint();
		}
	}

}
