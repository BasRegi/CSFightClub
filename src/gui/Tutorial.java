package gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel to display tutorial images
 * @author basilregi
 *
 */
@SuppressWarnings("serial")
public class Tutorial extends JPanel implements MouseListener
{
	private GUImodel model;
	private BufferedImage tutorial;
	private int tutpic = 0;
	private JButton skip;
	
	/**
	 * Contruct with model to change screens
	 * @param model
	 */
	public Tutorial(GUImodel model)
	{
		super();
		
		tutpic = 0;
		this.model = model;
		
		addMouseListener(this);
		
		skip = new JButton("Skip");
		skip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String screen = model.getState();
				String state = "";
				System.out.println(screen);
				if(screen.equals("L"))
				{
					state = "BY";
				}
				model.changeBoth(screen, state);
			}
		});
		add(skip);
		
		requestFocus();
	}
	
	/**
	 * Override paintcomoonent to progress through tutorial
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		try 
		{
			tutorial = ImageIO.read(new File("res/tut/tut"+tutpic+".png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		g.drawImage(tutorial, 2, 3, null);
		skip.setBounds(10, 10, 100, 30);
		
		
	}

	/**
	 * Action when mouse clicks panel
	 */
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		tutpic++;
		if(tutpic >= 7)
		{
			String screen = model.getState();
			String state = "";
			if(screen.equals("L"))
			{
				state = "BY";
			}
			model.changeBoth(screen, state);
			return;
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		//Do nothing
	}

	/**
	 * Action when mouse released after clicking panel
	 */
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		//Do Nothing
	}

	/**
	 * Action when mouse hovers over panel
	 */
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		//Do Nothing
	}

	/**
	 * Action when mouse hovers out of panel
	 */
	@Override
	public void mouseExited(MouseEvent e) 
	{
		//Do Nothing
	}
	
}