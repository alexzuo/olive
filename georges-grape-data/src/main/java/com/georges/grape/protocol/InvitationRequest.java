package com.georges.grape.protocol;

import java.util.ArrayList;
import java.util.List;

import com.georges.grape.data.Contact;

public class InvitationRequest {

    private String eventId;
    private String message;
    private List<Contact> contacts;

    @SuppressWarnings("unused")
    private InvitationRequest()
    {}
    public InvitationRequest(String eventId, String message, List<Contact> contacts)
    {
        this.eventId=eventId;
        this.message=message;
        this.contacts=contacts;
    }

    public String getEventId() {
        return eventId;
    }

    public String getMessage() {
        return message;
    }
    public List<Contact> getContacts() {
        return contacts;
    }
 
    
    
}
