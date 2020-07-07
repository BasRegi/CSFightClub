package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.Card;
import data.GameData;

/**
 * Class to contain the deck of cards
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Deck extends JPanel {

	private int id;
	private GameData gameData;
	private JList<String> cardList;
	private JScrollPane jsp;
	private JTextArea description;
	
	/**
	 * Constructor
	 * @param start Start value of list
	 * @param end End value of list
	 * @param gameData GameData
	 */
	public Deck(int start, int end, GameData gameData){
		
		super();
		this.setLayout(null);
		this.id = start;
		this.gameData = gameData;
		setBackground(new Color(0, 0, 0, 123));
		
		ArrayList<String> cardNameList = new ArrayList<String>();
		for (int i=start; i<=end; i++) cardNameList.add(
				String.valueOf(i-start)+": "+gameData.allCards.get(i).getName());
		cardList = new JList<String>(cardNameList.toArray(new String[cardNameList.size()]));
		cardList.setSize(300, 440);
		cardList.setBackground(new Color(0, 0, 0));
		cardList.setForeground(Color.WHITE);
		cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cardList.setAutoscrolls(true);
		cardList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				id = start + cardList.getSelectedIndex();
				cardList.ensureIndexIsVisible(cardList.getSelectedIndex());
				repaint();
			}
		});
		add(cardList);
		
		description = new JTextArea("");
		description.setFont(new Font("MS Gothic", Font.PLAIN, 24));
		description.setBounds(400, 0, 400, 440);
		description.setBackground(Color.BLACK);
		description.setForeground(Color.WHITE);
        add(description);
        
        
		jsp = new JScrollPane(cardList);
		jsp.setBounds(0, 0, 350, 450);
		jsp.setViewportView(cardList);
		add(jsp);
	}
	
	/**
	 * Override paintcomponent to animate
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Card card = gameData.allCards.get(id);
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
		description.setText(info);
		description.repaint();
	}
	
}
