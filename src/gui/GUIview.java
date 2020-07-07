package gui;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import data.BattleData;
import data.GameData;
import events.EventTrigger;
import events.EventUtil;

/**
 * Main GUI class which sets up the JFrame and controls rendering the screen changes model is updated
 * @author basilregi
 *
 */
public class GUIview implements Observer
{
	private GUImodel model;
	private JFrame frame;
	private final JComponent[] serverinputs;
	private Audio player;
	private EventTrigger eventTrigger;
	private GameData gameData;
	
	/**
	 * Constructor
	 * @param gameData GameData to update screen changes
	 * @param eventTrigger EventTrigger to manage event changes in GUI
	 * @param model GUImodel to observe
	 */
	public GUIview(GameData gameData, EventTrigger eventTrigger, GUImodel model)
	{
		this.eventTrigger = eventTrigger;
		this.gameData = gameData;
		this.model = model;
		this.model.addObserver(this);
		
		JTextField address = new JTextField();
		JTextField port = new JTextField();
		
		serverinputs = new JComponent[] {new JLabel("Address:"), address, new JLabel("Port:"), port};
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Welcome welcome = new Welcome(model, gameData, eventTrigger);
		frame.add(welcome);
		
		frame.setTitle("CS FIGHT CLUB");
		frame.setSize(1000,700);
	
		frame.setResizable(false);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		player = new Audio();
	}
	
	/**
	 * Called whenever there is a change in the model to re render a certain aspect on the screen
	 */
	public void render()
	{
		if(model.getScreen().equals("W"))
		{
			player.stop();
			frame.getContentPane().removeAll();
			Welcome welcome = new Welcome(model, gameData, eventTrigger);
			frame.add(welcome);
			frame.setTitle("CS FIGHT CLUB");
			frame.revalidate();
			gameData.currentScene = GameData.WELCOME_PAGE;
		}
		else if(model.getScreen().equals("H"))
		{
			player.play();
			frame.getContentPane().removeAll();
			Home home = new Home(model, gameData, eventTrigger);
			frame.add(home);
			frame.setTitle("HOME");
			frame.revalidate();
			gameData.currentScene = GameData.HOME_PAGE;
		}
		else if (model.getScreen().equals("CO"))
		{
			frame.getContentPane().removeAll();
			Collection collection = new Collection(model, gameData);
			frame.add(collection);
			frame.setTitle("Collection");
			frame.revalidate();
			gameData.currentScene = GameData.COLLECTION_PAGE;
		}
		else if (model.getScreen().equals("P"))
		{
			frame.getContentPane().removeAll();
			Page page = new Page(model, gameData);
			frame.add(page);
			frame.setTitle("Page");
			frame.revalidate();
			gameData.currentScene = GameData.PAGE_PAGE;
		}
		else if(model.getScreen().equals("T"))
		{
			frame.getContentPane().removeAll();
			Tutorial tut = new Tutorial(model);
			frame.add(tut);
			frame.setTitle("Tutorial");
			frame.revalidate();
			gameData.currentScene = GameData.TUTORIAL_PAGE;
			
		}
		else if(model.getScreen().equals("L"))
		{
			if(gameData.currentScene==GameData.LOBBY_PAGE) return;
			if(!gameData.getBattleData().single)
			{
				gameData.getBattleData().single = true;
				frame.getContentPane().removeAll();
				Lobby lobby = new Lobby(model, gameData, eventTrigger);
				frame.add(lobby);
				frame.setTitle("Lobby");
				frame.revalidate();
				lobby.requestFocus();
				gameData.currentScene = GameData.LOBBY_PAGE;
				return;
			}
			boolean serverconnection = false;
			if(model.getState().equals("BY"))
			{
				serverconnection = true;
				model.changeStateQuietly("");
			}
			while(serverconnection == false)
			{
				UIManager.put("OptionPane.okButtonText", "Connect");
				int result = JOptionPane.showConfirmDialog(null, serverinputs, "Connect", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION)
				{
					serverconnection = true;
					gameData.sysLockGUI = true;
					if (((JTextField)serverinputs[1]).getText().compareTo("") != 0) 
					{
						eventTrigger.myInput("clickLobby", ((JTextField)serverinputs[1]).getText(), ((JTextField)serverinputs[3]).getText());
						while (gameData.sysLockGUI) {EventUtil.sleep(50);}
					} 
					else 
					{
						if (gameData.debug) {gameData.currentScene = GameData.LOBBY_PAGE;}
					}
					if (gameData.currentScene == GameData.LOBBY_PAGE) 
					{
						serverconnection = true;
						gameData.sysLockGUI = true;
					}
					else 
					{
						serverconnection = false;
						JPanel panel = new JPanel();
						panel.add(new JLabel("Could not Connect"));
						UIManager.put("OptionPane.okButtonText", "Try Again");
						JOptionPane.showMessageDialog(null, panel, "Connection Error", JOptionPane.PLAIN_MESSAGE, null);
						UIManager.put("OptionPane.okButtonText", "OK");
					}
				}
				else
				{
					return;
				}
			}
			frame.getContentPane().removeAll();
			Lobby lobby = new Lobby(model, gameData, eventTrigger);
			frame.add(lobby);
			frame.setTitle("Lobby");
			frame.revalidate();
			lobby.requestFocus();
			gameData.currentScene = GameData.LOBBY_PAGE;
			
		}
		else if(model.getScreen().equals("S"))
		{
			frame.getContentPane().removeAll();
			Setting setting = new Setting(model, player, gameData, eventTrigger);
			frame.add((setting));
			frame.setTitle("Settings");
			frame.revalidate();
			setting.requestFocus();
			gameData.currentScene = GameData.SETTING_PAGE;
		}
		else if(model.getScreen().equals("B"))
		{
			player.stop();
			frame.getContentPane().removeAll();
			Battle battle = new Battle(model, 50, gameData, eventTrigger);
			gameData.currentScene = GameData.BATTLE_PAGE;
			eventTrigger.myInput("clickSingle");
			BattleData batData = gameData.getBattleData();
			while (!batData.myTurn && !batData.oppoTurn) {
				EventUtil.sleep(500);
			}
			frame.add(battle);
			frame.setTitle("Battle");
			frame.revalidate();
		}
	}
	

	/**
	 * Implementing Observer, observing Model and calling render every time model is updated
	 */
	@Override
	public void update(Observable o, Object arg) 
	{
		render();
	}

}
