package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import data.Charactor;
import data.GameData;
import events.EventTrigger;

/**
 * Panel to show settings for player changes in settings page
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class PlayerSettingsPanel extends JPanel implements ActionListener
{
	private Tiles tileset;
	private BufferedImage playertitle, charactertitle, usertitle;
	private JLabel pic;
	private Charactor c;
	private JComboBox<String> imagelist;
	private JTextField username;
	private JButton savebutton;
	
	/**
	 * Constructor
	 * @param dt GameData to get details
	 * @param trigger EventTrigger to update details
	 */
	public PlayerSettingsPanel(GameData dt, EventTrigger trigger)
	{
		super();
		setOpaque(true);
		setBackground(new Color(0, 0, 0));
		
		this.c = dt.getCharactor();
		
		tileset = new Tiles();
		playertitle = tileset.readsmallString("player settings", 2);
		charactertitle = tileset.readStringwSize("character", 16, 16);
		usertitle = tileset.readStringwSize("username", 16, 16);
		
		BufferedImage p1 = tileset.readCharacter(c.getPicID(), 0, 0, 125, 250);
		pic = new JLabel(new ImageIcon(p1), JLabel.CENTER);
		add(pic);
		
		String[] images = {"Character1", "Character2", "Character3", "Character4", "Character5", "Character6"};
		imagelist = new JComboBox<String>(images);
		imagelist.setSelectedIndex(c.getPicID() - 1);
		imagelist.addActionListener(this);
		add(imagelist);
		
		username = new JTextField(c.getName());
		add(username);
		
		savebutton = new JButton("Save Changes");
		savebutton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to save the settings?","Saving", JOptionPane.YES_NO_OPTION);
						if(dialogResult == JOptionPane.YES_OPTION)
						{
							trigger.myInput("updatePicID", "" + (imagelist.getSelectedIndex() + 1));
							trigger.myInput("updateUsername", username.getText());
						}
					}
				});
		add(savebutton);
		
	}
	
	/**
	 * Override paintcomponent to customise style
	 */
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		g.drawImage(playertitle, 10, 10, null);
		g.drawImage(charactertitle, 20, 345, null);
		g.drawImage(usertitle, 20, 420, null);
		pic.setBounds(150, 70, 125, 250);
		imagelist.setBounds(170, 340, 150, 30);
		username.setBounds(160, 412, 150, 30);
		savebutton.setBounds(320, 475, 150, 30);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		BufferedImage p1 = tileset.readCharacter(imagelist.getSelectedIndex() + 1, 0, 0, 125, 250);
		pic.setIcon(new ImageIcon(p1));
		repaint();
	}
}
