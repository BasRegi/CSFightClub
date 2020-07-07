package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is the class to play audio throughout the game. This runs on a separate thread and acts like a music player
 * ehich can be stopped, played and muted/unmuted.
 * @author basilregi
 *
 */
public class Audio {
	
	private static final ArrayList<File> tracks = new ArrayList<File>();
	private File currentTrack;
	private AudioInputStream audios;
	private Clip player;
	public boolean isPlaying = false;
	private FloatControl c;
	
	/**
	 * Constructor initialises player
	 */
	public Audio() 
	{
		tracks.add(new File("res/Rave.wav"));
		tracks.add(new File("res/Rock.wav"));
		tracks.add(new File("res/HipHop.wav"));

	    try 
	    {
	    	currentTrack = tracks.get(0);
			audios = AudioSystem.getAudioInputStream(currentTrack.toURI().toURL());
			player = AudioSystem.getClip();
		    player.open(audios);
		    c = (FloatControl)player.getControl(FloatControl.Type.MASTER_GAIN);
		    c.setValue(-30f);
		} 
	    catch (UnsupportedAudioFileException | IOException e) 
	    {
			e.printStackTrace();
		} 
	    catch (LineUnavailableException e) 
	    {
			e.printStackTrace();
		}  

	}
	
	/**
	 * Plays the player
	 */
	public void play() 
	{
		if(isPlaying)
		{
			return;
		}
		try 
		{
			audios = AudioSystem.getAudioInputStream(currentTrack.toURI().toURL());
			player = AudioSystem.getClip();
			player.open(audios);
			c = (FloatControl)player.getControl(FloatControl.Type.MASTER_GAIN);
		    c.setValue(-30f);
		    
		} 
		catch (UnsupportedAudioFileException | IOException e) 
		{
			e.printStackTrace();
		} 
		catch (LineUnavailableException e) 
		{
			e.printStackTrace();
		} 
		player.loop(Clip.LOOP_CONTINUOUSLY);
		isPlaying = true;
	}
	
	/**
	 * Pauses the player
	 */
	public void stop() 
	{
		
		player.stop();
		isPlaying = false;
	}
	
	/**
	 * Change track according to a track name
	 * @param track
	 */
	public void changeTrack(String track)
	{
		try 
		{
			switch(track)
			{
			case "Rave":
			{
				player.stop();
				player.close();
				audios = AudioSystem.getAudioInputStream(tracks.get(0).toURI().toURL());
				player.open(audios);
				player.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			}
			case "Rock":
			{
				player.stop();
				player.close();
				audios = AudioSystem.getAudioInputStream(tracks.get(1).toURI().toURL());
				player.open(audios);
				player.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			}
			case "HipHop":
			{
				player.stop();
				player.close();
				audios = AudioSystem.getAudioInputStream(tracks.get(2).toURI().toURL());
				player.open(audios);
				player.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			}
			}
		}
		catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) 
		{
			e.printStackTrace();
		}
		
		if(isPlaying)
		{
			player.start();
		}
	}
	
	/**
	 * Changes volume of player by a float
	 * @param level New value of volume
	 */
	public void setVolume(float level)
	{
		c = (FloatControl)player.getControl(FloatControl.Type.MASTER_GAIN);
		player.stop();
		player.setFramePosition(player.getFramePosition() % player.getFrameLength());
		player.flush();
	    c.setValue(level);
		player.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/**
	 * Getter for volume value
	 * @return volume value
	 */
	public float getVolume()
	{
		return c.getValue();
	}
	
	/**
	 * Getter for maximum value of volume
	 * @return maximum volume
	 */
	public float getMaximum()
	{
		return c.getMaximum();
	}
	
	/**
	 * Getter for minimum value of volume
	 * @return minimum volume
	 */
	public float getMinimum()
	{
		return c.getMinimum();
	}
	
	/**
	 * Getter for precision of volume
	 * @return precision
	 */
	public float getPrecision()
	{
		return c.getPrecision();
	}
}
