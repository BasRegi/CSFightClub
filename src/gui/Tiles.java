package gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class to extract the tiles from a png image
 * @author basilregi
 *
 */
public class Tiles 
{
	private Image tileset, fontset, smallfontset, characterset;
	private final int w = 32, h = 32, fw = 64, fh = 64, sfw = 16, sfh = 16;
	
	/**
	 * Constructor
	 */
	public Tiles()
	{
		try 
		{
			tileset = ImageIO.read(new File("res/icon0.png"));
			fontset = ImageIO.read(new File("res/fontthick.png"));
			smallfontset = ImageIO.read(new File("res/font.png"));
		} 
		catch (IOException e) 
		{
			System.out.println("Could not read tileset/fontset/characterset");
		}
	}
	
	/**
	 * Class to read floor tile from icon0.png by n and m
	 * @param n y value for floor tile
	 * @param m x value for floor tile
	 * @return Image of floor
	 */
	public BufferedImage readFloor(int n, int m)
	{
		BufferedImage floor = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		floor.getGraphics().drawImage(tileset, 0, 0, w, h, 32*n, m*32, 32*n + w, m*32 + h, null);
		return floor;
	}
	
	/**
	 * Reads a string and gives the image constructed from the font png file, only owkrs with lowercase
	 * @param s String to be converted
	 * @return BufferedImage of string in font style
	 */
	public BufferedImage readString(String s)
	{
		BufferedImage title = new BufferedImage(s.length()*fw, fh, BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < s.length(); x++)
		{
			int val = (int)s.charAt(x);
			val -= 97;
			if(val < 0)
			{
				val = 26;
			}
			title.getGraphics().drawImage(fontset, x*fw, 0, (x+1)*fw, fh, val*fw, 0,  (val+1)*fw, fh, null);
		}
		return title;
	}
	
	/**
	 * Same as above but with scale parameter
	 * @param s String to be read
	 * @param n Scale value
	 * @return BufferedImage of string s
	 */
	public BufferedImage readsmallString(String s, int n)
	{
		BufferedImage string = new BufferedImage(n*s.length()*sfw, n*sfh, BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < s.length(); x++)
		{
			int val = (int)s.charAt(x);
			val -= 97;
			if(val < 0)
			{
				val = 26;
			}
			string.getGraphics().drawImage(smallfontset, n*x*sfw, 0, n*(x+1)*sfw, n*sfh, val*sfw, 0,  (val+1)*sfw, sfh, null);
		}
		return string;
	}
	
	
	/**
	 * Same as above but with exact sizes
	 * @param s String to be read
	 * @param w width to get letter
	 * @param h height to get letter
	 * @return String s in font style
	 */
	public BufferedImage readStringwSize(String s, int w, int h)
	{
		BufferedImage string = new BufferedImage(s.length()*w, h, BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < s.length(); x++)
		{
			int val = (int)s.charAt(x);
			val -= 97;
			if(val < 0)
			{
				val = 26;
			}
			string.getGraphics().drawImage(smallfontset, x*w, 0, (x+1)*w, h, val*sfw, 0,  (val+1)*sfw, sfh, null);
		}
		return string;
	}
	
	/**
	 * Reads a specific character from font file
	 * @param charac Character to read
	 * @param x value fo character
	 * @param y value of character
	 * @param w width of image
	 * @param h height of image
	 * @return Character charac
	 */
	public BufferedImage readCharacter(int charac, int x, int y, int w, int h)
	{
		try 
		{
			characterset = ImageIO.read(new File("res/character" + charac + ".png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		BufferedImage character = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		character.getGraphics().drawImage(characterset, 0, 0, w, h, x*32, y*50, (x+1)*32, (y+1)*50, null);
		return character;
	}

}
