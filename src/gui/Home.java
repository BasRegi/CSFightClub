package gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GameData;
import events.EventTrigger;


@SuppressWarnings("serial")
public class Home extends JPanel
{
	private Tiles tileset;
	private BufferedImage floor, title, lobby, collection, single, settings, logout, version, help;
	private JLabel lobbybutton, collectionbutton, singlebutton, settingsbutton, logoutbutton, helpbutton;
	private PlayerInfo playerinfo;
	private boolean firstPlay = false;
	private int tutPic = 0;
	
	/**
	 * Constructor
	 * @param model GUIModel to update when screen needs to be changed
	 * @param gameData GameData to pass to listeners
	 * @param eventTrigger EventTrigger to update events in the screen
	 */
	public Home(GUImodel model, GameData gameData, EventTrigger eventTrigger)
	{
		super();
		
		tileset = new Tiles();
		floor = tileset.readFloor(3,0);
		
		title = tileset.readsmallString("home", 5);
		
		version = tileset.readStringwSize("cs fight club", 20, 20);
		
		help = tileset.readStringwSize("help", 20, 20);
		helpbutton = new JLabel(new ImageIcon(help), JLabel.LEFT);
		helpbutton.addMouseListener(new HomeListener("HELP", model, helpbutton, gameData, eventTrigger));
		helpbutton.setVisible(false);
		add(helpbutton);
		
		
		lobby = tileset.readsmallString("lobby", 2);
		lobbybutton = new JLabel(new ImageIcon(lobby), JLabel.LEFT);
		lobbybutton.addMouseListener(new HomeListener("L", model, lobbybutton, gameData, eventTrigger));
		lobbybutton.setVisible(false);
		add(lobbybutton);
		
		collection = tileset.readsmallString("collection", 2);
		collectionbutton = new JLabel(new ImageIcon(collection), JLabel.LEFT);
		collectionbutton.addMouseListener(new HomeListener("CO", model, collectionbutton, gameData, eventTrigger));
		collectionbutton.setVisible(false);;
		add(collectionbutton);
		
		single = tileset.readsmallString("single", 2);
		singlebutton = new JLabel(new ImageIcon(single), JLabel.LEFT);
		singlebutton.addMouseListener(new HomeListener("CA", model, singlebutton, gameData, eventTrigger));
		singlebutton.setVisible(false);
		add(singlebutton);
		
		settings = tileset.readsmallString("settings", 2);
		settingsbutton = new JLabel(new ImageIcon(settings), JLabel.LEFT);
		settingsbutton.addMouseListener(new HomeListener("S", model, settingsbutton, gameData, eventTrigger));
		settingsbutton.setVisible(false);
		add(settingsbutton);
		
		logout = tileset.readsmallString("log out", 2);
		logoutbutton = new JLabel(new ImageIcon(logout), JLabel.LEFT);
		logoutbutton.addMouseListener(new HomeListener("LO", model, logoutbutton, gameData, eventTrigger));
		logoutbutton.setVisible(false);
		add(logoutbutton);
		
		playerinfo = new PlayerInfo(gameData);
		playerinfo.setVisible(false);
        add(playerinfo);

        gameData.currentScene = GameData.HOME_PAGE;
        if (gameData.getCharactor().getPlayed()==0) {
        	firstPlay = true;
        	this.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					tutPic++;
					repaint();
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {}
				@Override
				public void mouseExited(MouseEvent arg0) {}
				@Override
				public void mousePressed(MouseEvent arg0) {}
				@Override
				public void mouseReleased(MouseEvent arg0) {}
        	});
        }
	}

	/**
	 * Override paincomponent to animate
	 */
	public void paintComponent(Graphics g) 
	{ 
		for(int x = 0; x < getWidth(); x += 32)
		{
			for(int y = 0; y < getHeight(); y += 32)
			{
				g.drawImage(floor, x, y, null);
			}
		}
		if (firstPlay && tutPic<=6) {
			if (tutPic==0) {
				BufferedImage tutorial = tileset.readStringwSize("tutorial", 32, 32);
				g.drawImage(tutorial, getWidth()/2-100, getHeight()/2, null);
				return;
			}
			try {
				
				BufferedImage tutImg = ImageIO.read(new File("/tut/tut"+tutPic+".png"));
				g.drawImage(tutImg, 0, 0, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} 
		lobbybutton.setVisible(true);
		collectionbutton.setVisible(true);
		singlebutton.setVisible(true);
		settingsbutton.setVisible(true);
		logoutbutton.setVisible(true);
		playerinfo.setVisible(true);
		helpbutton.setVisible(true);
		g.drawImage(title, getWidth()/2 - 128, 20, null);
		g.drawImage(version, getWidth() - 290, getHeight() - 30, null);
		lobbybutton.setBounds(getWidth()/100, 3*getHeight()/19, 375, 75);
		collectionbutton.setBounds(getWidth()/100, 6*getHeight()/19, 375, 75);
		singlebutton.setBounds(getWidth()/100, 9*getHeight()/19, 375, 75);
		settingsbutton.setBounds(getWidth()/100, 12*getHeight()/19, 375, 75);
		logoutbutton.setBounds(getWidth()/100, 15*getHeight()/19, 375, 75);
		playerinfo.setBounds(400, 135, 600, 480);
		helpbutton.setBounds(900, 20, 100, 30);
	} 
}
