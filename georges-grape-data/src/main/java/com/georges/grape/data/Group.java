package com.georges.grape.data;

import java.util.Date;

//@Document
public class Group {

	//@Id
	private String id;
	private String Name;
	private String description;
	private Date startDate;
	
	public Group(String groupName, String description) {
		super();
		this.Name = groupName;
		this.description = description;
	}
	
	
	public String getName() {
		return Name;
	}
	public void setName(String groupName) {
		this.Name = groupName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public Date getStartDate() {
		return startDate;
	}
	

}
