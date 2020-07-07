package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import data.GameData;
import events.EventTrigger;

@SuppressWarnings("serial")
public class Lobby extends JPanel implements Observer{
	private GUImodel model;
	private Tiles tileset;
	private BufferedImage floor, sidefloor, slomanfloor, title, send, logout, battle, p1, reception, sloman;
	private JTextField input;
	private JTextArea chatbox;
	private JButton sendbutton;
	private JLabel logoutbutton, battlebutton, character, receptionbutton, slomanbutton;
	private GameData gameData;
	private JScrollPane scrollbar;

	/**
	 * Constructor
	 * @param model GUImodel to change screens
	 * @param gameData GameData to update messages in lobby
	 * @param trigger EventTrigger to send messages
	 */
	public Lobby(GUImodel model, GameData gameData, EventTrigger trigger) {
		super();
		setLayout(null);
		this.gameData = gameData;
		this.model = model;
		model.addObserver(this);

		tileset = new Tiles();
		floor = tileset.readFloor(2, 0);
		sidefloor = tileset.readFloor(0, 1);
		slomanfloor = tileset.readFloor(7, 0);

		title = tileset.readsmallString("lobby", 5);

		chatbox = new JTextArea("Server: Welcome to the Lobby");
		chatbox.setEditable(false);
		chatbox.setBorder(BorderFactory.createLineBorder(Color.black));
		chatbox.setBounds(150, 250, 700, 250);
		DefaultCaret caret = (DefaultCaret)chatbox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(chatbox);
		
		scrollbar = new JScrollPane(chatbox);
		scrollbar.setBounds(150, 250, 700, 250);
		scrollbar.setViewportView(chatbox);
		add(scrollbar);

		input = new JTextField("Enter Message here");
		input.setBorder(BorderFactory.createLineBorder(Color.black));
		input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = input.getText();
					if (!message.equals("")) {
						input.setText("");
						trigger.myInput("sendMessage", null, message);
					}
				}
			}
		});
		add(input);

		p1 = tileset.readCharacter(gameData.getCharactor().getPicID(), 0, 0, 32, 50);
		character = new JLabel();
		character.setIcon(new ImageIcon(p1));
		character.setBounds(500, 600, 32, 50);

		MoveAction move = new MoveAction(character, this);
		move.start();

		CharacterListener list = new CharacterListener(character, move);
		list.addListeners();

		send = tileset.readStringwSize("send", 16, 16);
		sendbutton = new JButton();
		sendbutton.setIcon(new ImageIcon(send));
		// sendbutton.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		sendbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = input.getText();
				if (!message.equals("")) {
					//chatbox.append("\nYou: " + message);
					input.setText("");
					trigger.myInput("sendMessage", null, message);
				}
			}
		});
		add(sendbutton);

		battle = tileset.readStringwSize("match", 16, 16);
		battlebutton = new JLabel(new ImageIcon(battle), JLabel.LEFT);
		battlebutton.addMouseListener(new LobbyListener("B", model, battlebutton, trigger));
		add(battlebutton);

		reception = tileset.readStringwSize("help", 16, 16);
		receptionbutton = new JLabel(new ImageIcon(reception), JLabel.RIGHT);
		receptionbutton.addMouseListener(new LobbyListener("R", model, receptionbutton, trigger));
		receptionbutton.addMouseListener(new LobbyListener("T", model, receptionbutton, trigger));
		add(receptionbutton);

		sloman = tileset.readStringwSize("sloman store", 16, 16);
		slomanbutton = new JLabel(new ImageIcon(sloman), JLabel.CENTER);
		slomanbutton.addMouseListener(new LobbyListener("Slo", model, slomanbutton, trigger));
		add(slomanbutton);

		logout = tileset.readStringwSize("home", 16, 16);
		logoutbutton = new JLabel(new ImageIcon(logout), JLabel.CENTER);
		logoutbutton.addMouseListener(new LobbyListener("LO", model, logoutbutton, trigger));
		add(logoutbutton);

		add(character);
	}

	/**
	 * Override paintcomponent to animate
	 */
	public void paintComponent(Graphics g) {
		for (int x = 0; x < getWidth(); x += 32) {
			for (int y = 0; y < getHeight(); y += 32) {
				if (x <= 32 || x > getWidth() - 96) {
					if (y <= 400 && y >= 200) {
						g.drawImage(sidefloor, x, y, null);
					} else {
						g.drawImage(floor, x, y, null);
					}
				} else {
					g.drawImage(floor, x, y, null);
				}
			}
		}
		for (int x = 256; x < 736; x += 32) {
			for (int y = 0; y < 64; y += 32) {
				g.drawImage(slomanfloor, x, y, null);
			}
		}
		g.drawImage(title, getWidth() / 2 - 194, 120, null);
		sendbutton.setBounds(750, 500, 100, 50);
		input.setBounds(150, 502, 600, 48);
		battlebutton.setBounds(10, 300, 120, 30);
		receptionbutton.setBounds(825, 300, 150, 30);
		slomanbutton.setBounds(350, 20, 300, 30);
		logoutbutton.setBounds(getWidth() - 180, getHeight() - 30, 200, 30);
	}

	/**
	 * Update chatbox/lobby based on model state
	 */
	@Override
	public void update(Observable o, Object arg) 
	{
		if (model.getState().equals("UpdateMessage")){
			System.out.println("Lobby Message Update");
			ArrayList<String> messages = gameData.getLobbyData().messageHistory;
			String chat = "";
			for(int i = 0; i < messages.size(); i++)
			{
				chat += messages.get(i) + "\n";
			}
			chatbox.setText(chat);
		} else if (model.getState().equals("MatchStart")) {
			model.setState("");
			System.out.println("Match Started");
			gameData.getBattleData().single = false;
			model.changeScreen("B");
		}
	}
}
