package gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import ai.AIRandom;
import data.GameData;
import events.EventTrigger;

/**
 * Listener class for buttons on Home Screen
 * @author basilregi
 *
 */
public class HomeListener implements MouseListener
{
	private String type;
	private GUImodel model;
	private JLabel button;
	private GameData gameData;
	private EventTrigger eventTrigger;
	
	/**
	 * Constructor
	 * @param type Type of button
	 * @param model GUImodel to update screen change
	 * @param button Button to update style
	 * @param gameData Gamedata to update change in data
	 * @param eventTrigger EventTrigger to manage events on screen
	 */
	public HomeListener(String type, GUImodel model, JLabel button, GameData gameData, EventTrigger eventTrigger)
	{
		this.type = type;
		this.model = model;
		this.button = button;
		this.gameData = gameData;
		this.eventTrigger = eventTrigger;
	}

	/**
	 * Action when button object is clicked
	 */
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(type.equals("L"))
		{
			model.changeScreen("L");
		}
		else if(type.equals("CA"))
		{
			gameData.getBattleData().single = true;
			gameData.sysAIReady = true;
			AIRandom ai = new AIRandom(gameData, eventTrigger);
			ai.start();
			model.changeScreen("B");
		}
		else if(type.equals("CO"))
		{
			model.changeScreen("CO");
		}
		else if(type.equals("S"))
		{
			model.changeScreen("S");
		}
		else if(type.equals("LO"))
		{
			model.tutorialviewed = false;
			model.changeScreen("W");
		}
		else if(type.equals("HELP"))
		{
			model.changeBoth("T", "H");
		}
		
	}

	/**
	 * Action when mouse pressed on button object
	 */
	@Override
	public void mousePressed(MouseEvent e) 
	{
		button.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.black));	
	}

	/**
	 * Action when mouse is released after being pressed down on button
	 */
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		button.setBorder(BorderFactory.createEmptyBorder());
		mouseClicked(e);
	}

	/**
	 * Action when mouse hoevrs over button object
	 */
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		button.setHorizontalAlignment(JLabel.CENTER);
		button.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	/**
	 * Action when mouse hovers out button object
	 */
	@Override
	public void mouseExited(MouseEvent e) 
	{
		button.setHorizontalAlignment(JLabel.LEFT);
		button.setBorder(BorderFactory.createEmptyBorder());
	}

}
