package com.notbestlord.core.event;

public class RPGEvent {
	private final String eventType;
	private final String event;

	public RPGEvent(String eventType, String event) {
		this.eventType = eventType;
		this.event = event;
	}

	public String getEvent() {
		return event;
	}

	public String getEventType() {
		return eventType;
	}
}
