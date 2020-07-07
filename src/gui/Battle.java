package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import data.BattleData;
import data.Card;
import data.GameData;
import events.EventTrigger;
import events.EventUtil;

/**
 * Battle screen drawn on a JPanel
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Battle extends JPanel implements ActionListener {

	private Tiles tileset;
	private BufferedImage floor, title, giveup, endturn, labelHp, labelMana, labelHand;
	private JLabel giveupbutton, endturnbutton;
	private long startTime, elapsedTime;
	private Timer timer;
	private int countdown;
	private GameData gameData;
	private BufferedImage labelOpponent;
	private BufferedImage labelMyName;
	private BattleData batData;
	private CardPanel[] handCards = new CardPanel[10];
	private CardPanel[] myMinions = new CardPanel[10];
	private CardPanel[] oppoMinions = new CardPanel[10];
	private JTextArea cardDescription;
	private EventTrigger eventTrigger;
	private JLabel selectMyFaceBtn;
	private JLabel selectOppoFaceBtn;
	private boolean opponentError = false;
	private String errorMessage = "";
	private int turn = 0;
	private boolean resultShown = false;

	/**
	 * Constructor
	 * @param model GUI model to indicate changes to screen
	 * @param num Player number
	 * @param gameData GameData to update data during battle
	 * @param eventTrigger EventTrigger to trigger changes during battle
	 */
	public Battle(GUImodel model, int num, GameData gameData, EventTrigger eventTrigger) {
		super();
		this.gameData = gameData;
		this.eventTrigger = eventTrigger;
		batData = this.gameData.getBattleData();
		
		this.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (resultShown) {
					EventUtil.sleep(2000);
					if (gameData.getBattleData().single) {
						model.changeScreen("H");
					}
					else {
						model.changeScreen("L");
					}
				}
			}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseReleased(MouseEvent arg0) {}
		});

		tileset = new Tiles();
		floor = tileset.readFloor(1, 0);

		// labels
		title = tileset.readsmallString("battle", 3);
		labelMyName = tileset.readStringwSize(gameData.getCharactor().getName().toLowerCase(), 16, 20);
		labelHp = tileset.readStringwSize("hp", 20, 20);
		labelMana = tileset.readStringwSize("mana", 20, 20);
		labelHand = tileset.readStringwSize("hand", 20, 20);
		labelOpponent = tileset.readStringwSize(
				gameData.getBattleData().single?"computer":gameData.oppoName.toLowerCase(), 16, 20);


		// buttons
		giveup = tileset.readStringwSize("give up", 16, 16);
		giveupbutton = new JLabel(new ImageIcon(giveup), JLabel.RIGHT);
		giveupbutton.addMouseListener(
				new BattleListener("GU", model, giveupbutton, eventTrigger, gameData));
		add(giveupbutton);

		endturn = tileset.readStringwSize("next", 24, 24);
		endturnbutton = new JLabel(new ImageIcon(endturn), JLabel.RIGHT);
		endturnbutton.addMouseListener(
				new BattleListener("ET", model, endturnbutton, eventTrigger, gameData));
		add(endturnbutton);
		
		selectMyFaceBtn = new JLabel(new ImageIcon(labelMyName), JLabel.RIGHT);
		selectMyFaceBtn.addMouseListener(
				new BattleListener("SM", model, selectMyFaceBtn, eventTrigger, gameData));
		add(selectMyFaceBtn);
		
		selectOppoFaceBtn = new JLabel(new ImageIcon(labelOpponent), JLabel.RIGHT);
		selectOppoFaceBtn.addMouseListener(
				new BattleListener("SO", model, selectOppoFaceBtn, eventTrigger, gameData));
		add(selectOppoFaceBtn);
		
		// Card discription
		this.cardDescription = new JTextArea();
		cardDescription.setBounds(20, 160, 250, 280);
		cardDescription.setBackground(Color.BLACK);
		cardDescription.setForeground(Color.WHITE);
		cardDescription.setFont(new Font("MS Gothic", Font.PLAIN, 20));
		cardDescription.setLineWrap(true);
		cardDescription.setVisible(false);
		add(cardDescription);
		
		// card panels
		for (int i=0; i<10; i++){
			int index = i;
			handCards[i] = new CardPanel(batData);
			handCards[i].setBounds(550-120*i+(i/5 * 600), 480+(i/5 * 90), 110, 70);
			CardPanel cp = handCards[i];
			cp.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					//if (cp.getCard().selectable && batData.myTurn)
						eventTrigger.myInput("selectCard", String.valueOf(index));
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					cp.setMouseOn(true);
					cardDescription.setText(cardInfo(cp.getCard()));
					cardDescription.setVisible(true);
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					cp.setMouseOn(false);
					cardDescription.setVisible(false);
				}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
			});
			add(handCards[i]);
		}
		for (int i=0; i<10; i++){
			myMinions[i] = new CardPanel(batData);
			oppoMinions[i] = new CardPanel(batData);
			int index = i;
			CardPanel cpm = myMinions[i], cpo = oppoMinions[i];
			cpm.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					//if (cpm.getCard().selectable && batData.myTurn)
						eventTrigger.myInput("selectMyMinion", String.valueOf(index));
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					cpm.setMouseOn(true);
					cardDescription.setText(cardInfo(cpm.getCard()));
					cardDescription.setVisible(true);
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					cpm.setMouseOn(false);
					cardDescription.setVisible(false);
				}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
			});
			cpo.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					//if (cpo.getCard().selectable && batData.myTurn)
						eventTrigger.myInput("selectOppoMinion", String.valueOf(index));
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					cpo.setMouseOn(true);
					cardDescription.setText(cardInfo(cpo.getCard()));
					cardDescription.setVisible(true);
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					cpo.setMouseOn(false);
					cardDescription.setVisible(false);
				}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
			});
		}
		myMinions[0].setBounds(320, 160, 70, 60);
		myMinions[1].setBounds(405, 235, 70, 60);
		myMinions[2].setBounds(490, 310, 70, 60);
		myMinions[3].setBounds(575, 385, 70, 60);
		myMinions[4].setBounds(320, 235, 70, 60);
		myMinions[5].setBounds(405, 310, 70, 60);
		myMinions[6].setBounds(490, 385, 70, 60);
		myMinions[7].setBounds(320, 310, 70, 60);
		myMinions[8].setBounds(405, 385, 70, 60);
		myMinions[9].setBounds(320, 385, 70, 60);
		oppoMinions[0].setBounds(480, 140, 70, 60);
		oppoMinions[1].setBounds(565, 215, 70, 60);
		oppoMinions[2].setBounds(650, 290, 70, 60);
		oppoMinions[3].setBounds(735, 365, 70, 60);
		oppoMinions[4].setBounds(565, 140, 70, 60);
		oppoMinions[5].setBounds(650, 215, 70, 60);
		oppoMinions[6].setBounds(735, 290, 70, 60);
		oppoMinions[7].setBounds(650, 140, 70, 60);
		oppoMinions[8].setBounds(735, 215, 70, 60);
		oppoMinions[9].setBounds(735, 140, 70, 60);
		for (int i=0; i<10; i++){
			myMinions[i].setShowCost(false);
			oppoMinions[i].setShowCost(false);
			add(myMinions[i]);
			add(oppoMinions[i]);
		}
		
		gameData.currentScene = GameData.BATTLE_PAGE;
		this.setLayout(null); // moved into constructor from ActionPerformed:
								// only change layout in constructor
		startTime = 0;
		elapsedTime = 0;
		countdown = 790;

		if (timer == null) {

			timer = new Timer(150, this);
			startTime = System.currentTimeMillis(); // gets start time in
													// milliseconds
			timer.start();
		}
	}

	/**
	 * Override paintcomponent to animate panel
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw the floor
				for (int x = 0; x < getWidth(); x += 32) {
					for (int y = 0; y < getHeight(); y += 32) {
						g.drawImage(floor, x, y, null);
					}
				}
				g.drawImage(title, getWidth() / 2 - 134, 20, null);
		if (batData.battleEnd) {
			this.removeAll();
			String battleEndMessage;
			if (batData.myHP<=0 && batData.oppoHP<=0) {
				battleEndMessage = "draw game";
			} else if (batData.myHP<=0 || batData.myConcede){
				battleEndMessage = "you lose";
			} else if (batData.oppoHP<=0 || batData.oppoConcede){
				battleEndMessage = "you win!";
			} else {
				battleEndMessage = "click to exit";
			}
			BufferedImage img = tileset.readStringwSize(battleEndMessage, 32, 32);
			g.drawImage(img, getWidth()/2 - 150, getHeight()/2, null);
			resultShown  = true;
			return;
		}
		
		// background lines
		for (int i=0; i<10; i++){
			g.setColor(new Color(i*20, 0, 200-i*20, 123));
			g.drawLine(0, 140-i, 410, 140-i);
			g.drawLine(720, 450-i, 1000, 450-i);
			g.drawLine(400+i, 130, 720+i, 450);
		}

		// buttons
		giveupbutton.setBounds(825, 590, 150, 75);
		endturnbutton.setBounds(660, 590, 150, 75);
		selectMyFaceBtn.setBounds(700, 440, 200, 100);
		selectOppoFaceBtn.setBounds(780, 130, 200, 100);
		
		// selectable face
		g.setColor(new Color(200, 200, 100));
		if (batData.myFaceSelectable) 
			g.fillRect(700, 460, 280, 45);
		if (batData.oppoFaceSelectable)
			g.fillRect(830, 150, 150, 50);
		
		// hp mana boxes
		g.setColor(new Color(0, 0, 0, 123));
		g.fillRect(700, 460, 280, 140);
		g.fillRect(830, 150, 150, 270);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(700, 460, 280, 140);
		g.drawRect(830, 150, 150, 270);
		g.drawLine(830, 200, 980, 200);
		g.drawLine(700, 500, 980, 500);
		//g.drawImage(labelOpponent, 840, 170, null);
		g.drawImage(labelHp, 840, 210, null);
		g.drawImage(labelMana, 840, 280, null);
		g.drawImage(labelHand, 840, 350, null);
		//g.drawImage(labelMyName, 720, 470, null);
		g.drawImage(labelHp, 720, 520, null);
		g.drawImage(labelMana, 710, 570, null);
		g.setFont(new Font("MS Gothic", Font.PLAIN, 26));
		g.setColor(Color.GREEN);
		g.drawString(String.valueOf(batData.myHP), 820, 535);
		g.drawString(String.valueOf(batData.oppoHP), 900, 260);
		g.setColor(Color.CYAN);
		g.drawString(String.valueOf(batData.myMana), 820, 585);
		g.drawString(String.valueOf(batData.oppoMana), 900, 330);
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(String.valueOf(batData.oppoHand.size()), 900, 400);
		
		// handBoxes and hand
		g.setColor(new Color(0, 0, 0, 123));
		g.fillRect(-1, 460, 680, 200);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(-1, 460, 680, 200);
		for (int i=0; i<batData.myHand.size(); i++) {
			handCards[i].setCard(batData.myHand.get(i));
			handCards[i].setVisible(true);
			g.drawRect(549-120*i+(i/5 * 600), 479+(i/5 * 90), 111, 71);
		}
		for (int i=batData.myHand.size(); i<10; i++) {
			handCards[i].setVisible(false);
		}

		// time bar
		if (batData.myTurn){ 
			g.setColor(Color.black);
			g.fillRect(100, 80, 800, 25);
			if (countdown>400) g.setColor(Color.green);
			else if (countdown>150) g.setColor(Color.ORANGE);
			else g.setColor(Color.RED);
			g.fillRect(105, 85, countdown, 15);
			g.drawString("" + ((int)(120-((System.currentTimeMillis() - startTime)) / 1000.0)), 
				100, 70); // display remaining
		}
																							// time
		// paint the minions
		g.setColor(new Color(200, 200, 100));
		for (int i=0; i<batData.myMinions.size(); i++) {
			g.fillRect(myMinions[i].getX(), myMinions[i].getY(), 
					myMinions[i].getWidth(), myMinions[i].getHeight());
			myMinions[i].setCard(batData.myMinions.get(i));
			myMinions[i].setVisible(true);
		}
		for (int i=batData.myMinions.size(); i<10; i++) {
			myMinions[i].setVisible(false);
		}
		for (int i=0; i<batData.oppoMinions.size(); i++) {
			g.fillRect(oppoMinions[i].getX(), oppoMinions[i].getY(), 
					oppoMinions[i].getWidth(), oppoMinions[i].getHeight());
			oppoMinions[i].setCard(batData.oppoMinions.get(i));
			oppoMinions[i].setVisible(true);
		}
		for (int i=batData.oppoMinions.size(); i<10; i++) {
			oppoMinions[i].setVisible(false);
		}
		
		// paint message
		if (batData.myTurn){
			if (opponentError) {
				errorMessage = batData.errorMessage;
				opponentError = false;
			}
			if (errorMessage !=batData.errorMessage) {
				errorMessage = batData.errorMessage;
				g.setColor(new Color(250, 180, 160));
				g.setFont(new Font("MS Gothic", Font.BOLD, 20));
				g.drawString(errorMessage, 300, 460);
			}
		} else {
			if (batData.errorMessage.length()>0) {
				opponentError=true;
				errorMessage = batData.errorMessage;
			}
		}
	}

	/**
	 * Listener for next button
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (turn != batData.turn) {
			turn = batData.turn;
			countdown = 790;
			startTime = System.currentTimeMillis();
		}
		elapsedTime = System.currentTimeMillis() - startTime;
		if (elapsedTime < (120000)) {
			if (batData.myTurn) {
				countdown--; 
				if (countdown==0) eventTrigger.myInput("nextRound");
			} else {
				countdown = 790;
				startTime = System.currentTimeMillis();
			}
		} else {
			timer.stop();
			if (timer == null) {
				timer = new Timer(1000, this);
				timer.start();
			}
		}
		repaint();
	}
	
	/**
	 * To get information of a card to display on panel
	 * @param card Card type to get info for
	 * @return String of info in appropriate format
	 */
	private String cardInfo(Card card){
		if (card==null) return "";
		String info = card.getName() + "\n";
		if (card.isMinion()){
			info += "\tMINION\n";
			info += "\nCost    : " + card.getCost();
			info += "\nDefence : " + card.getMaxDefence();
			info += "\nAttack  : " + card.getMaxAttack();
			info += "\nEffect  : \n" + card.getEffect();
		} else {
			info += "\tCARD\n";
			info += "\nCost    : " + card.getCost();
			info += "\nEffect  : \n" + card.getEffect();
		}
		return info;
	}

}