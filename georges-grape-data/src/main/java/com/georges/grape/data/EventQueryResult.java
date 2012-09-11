package com.georges.grape.data;

public class EventQueryResult {
	
	public EventQueryResult()
	{
		
	}
	public EventQueryResult(Event event, double distance) {
		super();
		this.event = event;
		this.distance = distance;
	}
	private Event event;
	private double distance;
	public Event getEvent() {
		return event;
	}
	public double getDistance() {
		return distance;
	}
	
	
}
