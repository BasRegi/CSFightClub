package gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

/**
 * Class to implement listener on character in lobby
 * @author basilregi
 *
 */
public class CharacterListener 
{
	private JLabel ch;
	private MoveAction move;
	
	/**
	 * Contruct with image of character in JLabel and move action
	 * @param ch JLabel of character
	 * @param move - MoveAction
	 */
	public CharacterListener(JLabel ch, MoveAction move)
	{
		this.ch = ch;
		this.move = move;
	}
	
	/**
	 * Adds the listeners to the JLabel
	 */
	@SuppressWarnings("serial")
	public void addListeners()
	{
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "UP_PRESSED");
		ch.getActionMap().put("UP_PRESSED", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(true);
				move.setKey(1);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UP_RELEASED");
		ch.getActionMap().put("UP_RELEASED", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(false);
				move.setKey(0);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "DOWN_PRESSED");
		ch.getActionMap().put("DOWN_PRESSED", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(true);
				move.setKey(2);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "DOWN_RELEASED");
		ch.getActionMap().put("DOWN_RELEASED", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(false);
				move.setKey(0);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "L_PRESSED");
		ch.getActionMap().put("L_PRESSED", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(true);
				move.setKey(3);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "L_RELEASED");
		ch.getActionMap().put("L_RELEASED", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(false);
				move.setKey(0);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "R_PRESSED");
		ch.getActionMap().put("R_PRESSED", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(true);
				move.setKey(4);
			}
		});
		ch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "R_RELEASED");
		ch.getActionMap().put("R_RELEASED", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				move.setIsPressed(false);
				move.setKey(0);
			}
		});
	}

}
