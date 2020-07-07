package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import data.GameData;
import events.EventTrigger;
import events.EventUtil;

/**
 * Listener for buttons on Battle Screen
 * @author basilregi
 *
 */
public class BattleListener implements MouseListener {

	private String type;
	private GUImodel model;
	private JLabel button;
	private Tiles tileset = new Tiles();
	private BufferedImage img;
	private EventTrigger eventTrigger;
	private GameData gameData;

	/**
	 * Constructor
	 * @param type - Type of button
	 * @param model - Model class to cause change
	 * @param button - Button object
	 * @param eventTrigger - Event Trigger to cause change
	 * @param gameData - GameData to update battle data
	 */
	public BattleListener(String type, GUImodel model, JLabel button, EventTrigger eventTrigger, GameData gameData) {
		this.type = type;
		this.model = model;
		this.button = button;
		this.gameData = gameData;
		this.eventTrigger = eventTrigger;
	}

	/**
	 * Action for when mouse clicks on button
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (type.equals("GU")) {
			eventTrigger.myInput("giveUp");
			EventUtil.sleep(200);
			if (gameData.getBattleData().single) {
				model.changeScreen("H");
			}
			else {
				model.changeScreen("L");
			}

		} else if (type.equals("ET")) {
			if (gameData.getBattleData().myTurn){
				eventTrigger.myInput("nextRound");
				EventUtil.sleep(50);
				if (gameData.sysAIReady || !gameData.getBattleData().single){
					EventUtil.sleep(50);
				} else {
					eventTrigger.aiInput("nextRound");
				}
			}
		} else if (type.equals("SM")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().myFaceSelectable){
				eventTrigger.myInput("selectMyFace");
				EventUtil.sleep(50);
			//}
		} else if (type.equals("SO")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().oppoFaceSelectable){
				eventTrigger.myInput("selectOppoFace");
				EventUtil.sleep(50);
			//}
		}
	}

	/**
	 * Action for when mouse is pressed down on button
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}

	/**
	 * Action for when mouse is released from button
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		button.setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * Action for when mouse hovers over button
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		if (type.equals("GU")) {
			img = tileset.readStringwSize("give up", 20, 20);
			button.setIcon(new ImageIcon(img));
			//button.repaint();

		} else if (type.equals("ET")) {
			img = tileset.readStringwSize("next", 28, 28);
			button.setIcon(new ImageIcon(img));
			//button.repaint();
		} else if (type.equals("SM")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().myFaceSelectable){
				img = tileset.readStringwSize(gameData.getCharactor().getName().toLowerCase(), 18, 22);
				button.setIcon(new ImageIcon(img));
			//}
		} else if (type.equals("SO")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().oppoFaceSelectable){
				img = tileset.readStringwSize(gameData.getBattleData().single?"computer":gameData.oppoName.toLowerCase(), 18, 22);
				button.setIcon(new ImageIcon(img));
			//}
		}
	}

	/**
	 * Action for when mouse hovers out of button
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (type.equals("GU")) {
			img = tileset.readStringwSize("give up", 16, 16);
			button.setIcon(new ImageIcon(img));
			//button.repaint();
		} else if (type.equals("ET")) {
			img = tileset.readStringwSize("next", 24, 24);
			button.setIcon(new ImageIcon(img));
			//button.repaint();
		} else if (type.equals("SM")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().myFaceSelectable){
				img = tileset.readStringwSize(gameData.getCharactor().getName().toLowerCase(), 16, 20);
				button.setIcon(new ImageIcon(img));
			//}
		} else if (type.equals("SO")){
			//if (gameData.getBattleData().myTurn && gameData.getBattleData().oppoFaceSelectable){
				img = tileset.readStringwSize(gameData.getBattleData().single?"computer":gameData.oppoName.toLowerCase(), 16, 20);
				button.setIcon(new ImageIcon(img));
			//}
		}

	}

}
