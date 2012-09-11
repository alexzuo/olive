package com.georges.grape.data;

import java.util.Date;

public class ChatMessage {
	private String id;
    private String userId;
    private Date date;
	private String eventId;
	private String message;
	
	//needed by JSON
	@SuppressWarnings("unused")
    private ChatMessage()
	{
	    
	}
    public ChatMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }
    
    public String getUserId() {
        return userId;
    }
    public Date getDate()  {
        return date;
    }
    
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatMessage [id=" + id + ", eventId=" + eventId + ", userId=" + userId + ", message=" + message + "]";
    }
	
}
