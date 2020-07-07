package gui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import data.Card;
import events.EventTrigger;

import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;

/**
 * 
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class MinionPanel extends JPanel {
	private Card card;
	public int id;
	private int def;
	private int atk;
	private boolean selectable;
	private JLabel lblDefend;
	private JLabel lblAttack;
	private JTextArea txtrMinion;
	
	/**
	 * Constructor
	 * @param card Card to be drawn
	 * @param id ID of card
	 * @param mine Whether its your card or not
	 * @param eventTrigger EventTrigger to update changes
	 */
	public MinionPanel(Card card, int id, boolean mine, EventTrigger eventTrigger) {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (getCard().selectable) eventTrigger.myInput(
						mine?"selectMyMinion":"selectOppoMinion", String.valueOf(id));
			}
		});
		this.setCard(card);
		this.def = card.getDefence();
		this.atk = card.getAttack();
		this.id = id;
		this.selectable = card.selectable;
		
		setBackground(Color.GRAY);
		setBorder(new LineBorder(Color.WHITE));
		setLayout(new BorderLayout(0, 0));
		
		txtrMinion = new JTextArea(card.getName());
		txtrMinion.setEditable(false);
		txtrMinion.setLineWrap(true);
		txtrMinion.setForeground(Color.WHITE);
		txtrMinion.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (getCard().selectable) eventTrigger.myInput(
						mine?"selectMyMinion":"selectOppoMinion", String.valueOf(id));
			}
		});
		txtrMinion.setFont(new Font("Lucida Bright", Font.PLAIN, 20));
		txtrMinion.setBackground(Color.GRAY);
		add(txtrMinion, BorderLayout.CENTER);
		
		lblDefend = new JLabel(String.valueOf(def));
		lblDefend.setFont(new Font("Lucida Bright", Font.PLAIN, 16));
		lblDefend.setForeground(Color.GREEN);
		add(lblDefend, mine? BorderLayout.NORTH : BorderLayout.SOUTH);
		
		lblAttack = new JLabel(String.valueOf(atk));
		lblAttack.setFont(new Font("Lucida Bright", Font.PLAIN, 16));
		lblAttack.setForeground(Color.PINK);
		add(lblAttack, mine? BorderLayout.SOUTH : BorderLayout.NORTH);
	
		refresh();
	}
	
	/**
	 * Refresh the cards
	 */
	public void refresh(){
		if (getCard().getDefence() != def) {
			atk = getCard().getDefence();
			lblAttack.setText(String.valueOf(def));
		}
		if (getCard().getAttack() != atk) {
			atk = getCard().getAttack();
			lblAttack.setText(String.valueOf(atk));
		}
		if (getCard().selectable != selectable) {
			selectable = getCard().selectable;
			this.setBackground(selectable ? Color.GRAY : Color.DARK_GRAY);
			txtrMinion.setBackground(selectable ? Color.GRAY : Color.DARK_GRAY);
		}
	}

	/**
	 * Get the JLabel containing defence value
	 * @return JLabel
	 */
	public JLabel getLblDefend() {
		return lblDefend;
	}
	
	/**
	 * Get the JLabel containing attack value
	 * @return JLabel
	 */
	public JLabel getLblAttack() {
		return lblAttack;
	}
	
	/**
	 * Get the JLabel containing minion info
	 * @return JLabel
	 */
	public JTextArea getLblMinion() {
		return txtrMinion;
	}

	/**
	 * Get the Card
	 * @return Card
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * Change the card
	 * @param card new Card to be set
	 */
	public void setCard(Card card) {
		this.card = card;
	}
}
