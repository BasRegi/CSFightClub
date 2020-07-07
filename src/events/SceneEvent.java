package events;

import java.util.ArrayList;

/**
 * All SceneEvent classes like BattleEvent, CollectionEvent
 * 	   should extends this class
 * 
 * EventManager will put event into eventPool and then
 *     You may use takeEvent() to take it out
 */
public abstract class SceneEvent {
	private ArrayList<Event> eventPool = new ArrayList<Event>();
	/**
	 * Executes the scene
	 */
	public abstract void start();
	
	public void newEvent(Event event){
		eventPool.add(event);
		System.out.println("DEBUG: event \""+event.getMessage()+"\" added to the scene " + this.getClass().getName());
	}
	
	/**
	 * takeEvent
	 * take the event out from the eventPool.
	 * @return the first event in the event pool
	 * 
	 * if there is no event in the pool, it will return null
	 */
	protected Event takeEvent(){
		if (!eventPool.isEmpty()){
			Event event = eventPool.get(0);
			eventPool.remove(0);
			return event;
		}
		return null;
	}
	
}
