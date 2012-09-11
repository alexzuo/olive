package com.georges.grape.data;

import java.util.Arrays;
import java.util.Date;


public class Event {

	private String id;
	private String ownerId;
	private String[] memberIds;
	private String subject;
	private Date date;
	private String address;
	private double[] geolocation; //[longitude, latitude]
	private String description;
	private Boolean open;
	private String postPictureUri;
	
	public Event()
	{
		
	}
	public Event(String subject, Date date, String address,
			String description, Boolean open) {
		super();
		this.subject = subject;
		this.date = date;
		this.address = address;
		this.description = description;
		this.open = open;
	}
	
	public String getOwnerId() {
	    return ownerId;
	}
	
	public String[] getMemberIds() {
	    return memberIds;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public String getId() {
		return id;
	}
	public double[] getGeolocation() {
		return geolocation;
	}
	
	public String getPostPictureUri() {
        return postPictureUri;
    }
    @Override
	public String toString() {
		String geoStr;
		if(geolocation!=null)
			geoStr="geo[latitude=" + geolocation[1]+", longitude=" +geolocation[0] + "]";
		else
			geoStr="geo[latitude=null, longitude=null]";
		
		return "Event [id=" + id + ",ownerId="+ ownerId+ ", subject=" + subject + ", date=" + date
				+ ", address=" + address + ","+geoStr+", description="
				+ description + ", memembersId="+ Arrays.toString(this.memberIds)+", open=" + open + "]";
	}
	@Override
	public boolean equals(Object object) {
		Event event = (Event)object;
		return (this.id.equals(event.getId()));
	}
}
