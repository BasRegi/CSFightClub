package gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import data.GameData;
import events.EventTrigger;
import events.EventUtil;

/**
 * Panel for welcome screen asking for login
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Welcome extends JPanel
{
	private GUImodel model;
	private Tiles tileset;
	private BufferedImage floor, title, userlabel, passlabel;
	private JTextField username;
	private JPasswordField password;
	private JButton login;
	private EventTrigger eventTrigger;
	private GameData gameData;
	private JButton register;
    
	/**
	 * Constructor
	 * @param model Model to change screens
	 * @param gameData GameData to validate login
	 * @param eventTrigger EventTrigger to trigger login event
	 */
	public Welcome(GUImodel model, GameData gameData, EventTrigger eventTrigger)
	{
		super();
		this.eventTrigger = eventTrigger;
		this.gameData = gameData;
		this.model = model;
		tileset = new Tiles();
		floor = tileset.readFloor(3,0);
		
		title = tileset.readString("cs fight club");
		
		userlabel = tileset.readsmallString("username", 2);
		
		passlabel = tileset.readsmallString("password", 2);
		
		username = new JTextField("Player1");
		username.setFont(new Font("MS Gothic", Font.PLAIN, 20));
		add(username);
		
		password = new JPasswordField("password1");
		password.setFont(new Font("MS Gothic", Font.PLAIN, 20));
		password.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==10) login(); 
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		add(password);
		
		login = new JButton("Login");
		login.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e){ login();}
		});
		add(login);
		
		register = new JButton("Register");
		register.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e){ register();}
		});
		add(register);
		gameData.currentScene = GameData.WELCOME_PAGE;
	}
	
	/**
	 * Validate login
	 */
	protected void login() {
		eventTrigger.myInput("login", username.getText(), new String(password.getPassword()));
		  gameData.sysLockGUI = true;
		  while (gameData.sysLockGUI) EventUtil.sleep(50);
		  if (gameData.currentScene == GameData.WELCOME_PAGE) {

		  } else {
			  username.setText("");
			  password.setText("");
			  model.changeScreen("H");
		  }
	}
	
	/**
	 * Register new player
	 */
	protected void register() {
		eventTrigger.myInput("register", username.getText(), new String(password.getPassword()));
		gameData.sysLockGUI = true;
		while (gameData.sysLockGUI) EventUtil.sleep(50);
		if (gameData.currentScene == GameData.WELCOME_PAGE) {

		  } else {
			  username.setText("");
			  password.setText("");
			  model.changeScreen("H");
		  }
	}

	/**
	 * Override Paintcomponent to render and customise style
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
		g.drawImage(title, getWidth()/10, getHeight()/7, null);
		username.setBounds(3*getWidth()/8+70, 3*getHeight()/8, 400, 50);
		password.setBounds(3*getWidth()/8+70, 4*getHeight()/8, 400, 50);
		login.setBounds(3*getWidth()/8+150, 5*getHeight()/8, 200, 50);
		register.setBounds(3*getWidth()/8-250+150, 5*getHeight()/8, 200, 50);
		g.drawImage(userlabel, getWidth()/10+70, 7*getHeight()/18 , null);
		g.drawImage(passlabel, getWidth()/10+70, 11*getHeight()/21 , null);
	} 

}
