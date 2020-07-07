package events.battle;

import data.Card;
import events.EventSource;

public class BattleEffect {
	private Card card = null;
	private String effect = null;
	private EventSource event = null;
	
	public BattleEffect(Card c, String e, EventSource ev) {
		this.card = c;
		this.effect = e;
		this.event = ev;
	}
	
	public Card getCard() {
		return card;
	}
	
	public void setCard(Card newCard) {
		card = newCard;
	}
	
	public String getEffect() {
		return effect;
	}
	
	public void setString(String newEffect) {
		effect = newEffect;
	}
	
	public EventSource getEventSource() {
		return event;
	}
	
	public void setEventSource(EventSource newEvent) {
		event = newEvent;
	}
	
}