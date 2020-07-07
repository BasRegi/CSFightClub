package events;

public class Event {
	private final String message;
	private String[] args;
	private final EventSource eventSource;
	
	public Event(String message, EventSource eventSource, String ...args){
		this.message = message;
		this.eventSource = eventSource;
		this.args = args;
	}
	
	public String getMessage(){
		return message;
	}
	
	public EventSource getEventSource(){
		return eventSource;
	}

	public String[] getArgs(){
		return args;
	}
}
