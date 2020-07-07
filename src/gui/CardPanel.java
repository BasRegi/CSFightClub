package gui;

import java.awt.Graphics;
import javax.swing.JPanel;
import data.BattleData;
import data.Card;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.SwingConstants;

/**
 * JPanel to show information of card
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class CardPanel extends JPanel {

	private Card card;
	private JLabel cardAttack;
	private JLabel cardDefence;
	private JLabel cardName;
	private JLabel cardCost;
	private BattleData batData;
	
	private boolean showCost = true;
	@SuppressWarnings("unused")
	private boolean selectable = true;
	private boolean mouseOn;
	
	/**
	 * Constructor
	 * @param batData - to get card data
	 */
	public CardPanel(BattleData batData) {
		this.batData = batData;
		setBackground(new Color(102, 51, 0));
		setLayout(new BorderLayout(0, 0));
		
		cardName = new JLabel("Card");
		cardName.setHorizontalAlignment(SwingConstants.CENTER);
		cardName.setFont(new Font("MS Gothic", Font.PLAIN, 20));
		cardName.setForeground(Color.WHITE);
		add(cardName, BorderLayout.NORTH);
		
		cardDefence = new JLabel("");
		cardDefence.setHorizontalAlignment(SwingConstants.CENTER);
		cardDefence.setVerticalAlignment(SwingConstants.BOTTOM);
		cardDefence.setFont(new Font("MS Gothic", Font.PLAIN, 28));
		cardDefence.setForeground(Color.GREEN);
		add(cardDefence, BorderLayout.WEST);
		
		cardAttack = new JLabel("");
		cardAttack.setHorizontalAlignment(SwingConstants.CENTER);
		cardAttack.setVerticalAlignment(SwingConstants.BOTTOM);
		cardAttack.setFont(new Font("MS Gothic", Font.PLAIN, 28));
		cardAttack.setForeground(new Color(250, 100, 100));
		add(cardAttack, BorderLayout.EAST);
		
		cardCost = new JLabel("");
		cardCost.setHorizontalAlignment(SwingConstants.CENTER);
		cardCost.setVerticalAlignment(SwingConstants.BOTTOM);
		cardCost.setFont(new Font("MS Gothic", Font.PLAIN, 28));
		cardCost.setForeground(Color.CYAN);
		add(cardCost, BorderLayout.CENTER);
		
	}
	
	/**
	 * Override paintComponent to animate
	 */
	public void paintComponent(Graphics g) {
		g.setColor(new Color(20, 20, 0));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (card==null) return;
		if (!card.selectable || !batData.myTurn) {
			cardAttack.setForeground(Color.DARK_GRAY);
			cardDefence.setForeground(Color.DARK_GRAY);
			cardCost.setForeground(Color.DARK_GRAY);
			cardName.setForeground(Color.DARK_GRAY);
			this.setEnabled(false);
		} else {
			cardAttack.setForeground(new Color(250, 100, 100));
			cardDefence.setForeground(Color.GREEN);
			cardCost.setForeground(Color.CYAN);
			cardName.setForeground(Color.WHITE);
			this.setEnabled(true);
			if (mouseOn) {
				g.setColor(new Color(60, 60, 20));
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		}
		if (card.isMinion()) {
			cardAttack.setText(String.valueOf(card.getAttack()));
			cardDefence.setText(String.valueOf(card.getDefence()));
		} else {
			cardAttack.setText("");
			cardDefence.setText("");
		}
		if (showCost) cardCost.setText(String.valueOf(card.getCost()));
		cardName.setText(String.valueOf(card.getName()));
	}
	
	/**
	 * Get a card object
	 * @return
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * Set the current card
	 * @param card
	 */
	public void setCard(Card card) {
		this.card = card;
		repaint();
	}
	
	/**
	 * Set that the mouse is over the card
	 * @param mouseOn
	 */
	public void setMouseOn(boolean mouseOn){
		this.mouseOn = mouseOn;
	}
	
	/**
	 * Shows cost of card
	 * @param showCost
	 */
	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		repaint();
	}
	
	/**
	 * Makes the card selectable
	 * @param selectable boolean value
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		repaint();
	}

	/**
	 * Attack value of card
	 * @return JLabel containing attack value
	 */
	public JLabel getCardAttack() {
		return cardAttack;
	}
	
	/**
	 * Get the defence value of card
	 * @return JLabel containing defence value 
	 */
	public JLabel getCardDefence() {
		return cardDefence;
	}
	
	/**
	 * Get name of card
	 * @return JLabel containing card name
	 */
	public JLabel getCardName() {
		return cardName;
	}
	
	/**
	 * Get the cost of a card
	 * @return JLabel containing cost of card
	 */
	public JLabel getCardCost() {
		return cardCost;
	}
}
