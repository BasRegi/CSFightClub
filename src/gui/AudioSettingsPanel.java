package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class to hold all the audio settings in JPanel
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class AudioSettingsPanel extends JPanel implements ChangeListener, ActionListener
{
	private Audio aud;
	private JSlider volume;
	private JButton mutebutton;
	private JButton unmutebutton;
	private Tiles tileset;
	private BufferedImage audiotitle, volumelabel, tracklabel;
	private JComboBox<String> tracklist;
	
	/**
	 * Contructor with the audio player
	 * @param aud Audio object
	 */
	public AudioSettingsPanel(Audio aud)
	{
		super();
		setOpaque(true);
		setBackground(new Color(0, 0, 0));
		
		tileset = new Tiles();
		audiotitle = tileset.readsmallString("audio settings", 2);
		volumelabel = tileset.readsmallString("volume", 1);
		tracklabel = tileset.readsmallString("track", 1);
		
		this.aud = aud;
		
		mutebutton = new JButton("Mute");
		mutebutton.setBackground(new Color(0, 0, 0));
		mutebutton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						aud.setVolume(-80f);
						mutebutton.setEnabled(false);
						unmutebutton.setEnabled(true);
					}
				});
		add(mutebutton);
		
		unmutebutton = new JButton("Un-mute");
		unmutebutton.setBackground(new Color(0, 0, 0));
		unmutebutton.setEnabled(false);
		unmutebutton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						float vol = volume.getValue() - 50;
						aud.setVolume(vol);
						mutebutton.setEnabled(true);
						unmutebutton.setEnabled(false);
					}
				});
		add(unmutebutton);
		
		int currentVol = Math.round(aud.getVolume());
		if(currentVol < -50)
		{
			currentVol = -50;
			mutebutton.setEnabled(false);
			unmutebutton.setEnabled(true);
		}
		volume = new JSlider(JSlider.HORIZONTAL, 0, 50, currentVol+50);
		volume.setMajorTickSpacing(10);
		volume.setMinorTickSpacing(1);
		volume.setPaintTicks(true);
		volume.addChangeListener(this);
		volume.setBackground(new Color(0, 0, 0));
		add(volume);
		
		String[] tracks = {"Rave", "Rock", "HipHop"};
		tracklist = new JComboBox<String>(tracks);
		tracklist.setSelectedIndex(0);
		tracklist.addActionListener(this);
		add(tracklist);	
	}

	/**
	 * Listener for slider
	 */
	@Override
	public void stateChanged(ChangeEvent e) 
	{
		JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) 
	    {
	    	float volume = (float)source.getValue() - 50;
	    	aud.setVolume(volume);
	    }
	}
	
	/**
	 * Override paintcomponent
	 */
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		g.drawImage(audiotitle, 10, 10, null);
		g.drawImage(volumelabel, 10, 90, null);
		volume.setBounds(110, 70, 350, 50);
		mutebutton.setBounds(160, 120, 100, 30);
		unmutebutton.setBounds(280, 120, 100, 30);
		g.drawImage(tracklabel, 10, 190, null);
		tracklist.setBounds(110, 185, 100, 30);

	}

	/**
	 * Listener for option box
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>)e.getSource();
        String track = (String)cb.getSelectedItem();
        aud.changeTrack(track);
	}

}
