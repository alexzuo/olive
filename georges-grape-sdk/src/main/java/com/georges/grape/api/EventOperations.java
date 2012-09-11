package com.georges.grape.api;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.georges.grape.data.ChatMessage;
import com.georges.grape.data.Contact;
import com.georges.grape.data.Event;
import com.georges.grape.data.GeoLocation;
import com.georges.grape.data.GrapeException;
import com.georges.grape.data.GrapeUser;

/**
 * Defines operations for retrieving data about events
 * @author Alex Zuo
 */
public interface EventOperations {

    public enum Order {
        ASCENDING, 
        DESCENDING
    }
	/**
	 * Retrieve data for a specified event.
	 * @param eventId the ID of the event
	 * @return a {@link Event} object
	 * @throws GrapeException if there is an error while communicating with Grape.
	 */
	Event getEvent(String eventId);
	
	/**
	 * Create an event with specified event data.
	 * @param e the event to be created with event Id as null
	 * @return the event ID assigned by Grape 
	 * @throws GrapeException if there is an error while communicating with Grape.
	 */
	String createEvent(Event e);
	
	/**
	 * Update an event with specified event data.
	 * @param e the event to be updated, event ID must exist
	 * @throws GrapeException if there is an error while communicating with Grape.
	 */
	void updateEvent(Event e);
	
	/**
	 * Delete an event
	 * @param eventId the ID of the event
	 * @throws GrapeException if there is an error while communicating with Grape.
	 */
	void deleteEvent(String eventId);
	
	/**
	 * Search open events.
	 * 
	 * To query events for the first time, you don't have a token:
	 *  <pre>
	 *   eventOperations().search(null, null, Order.DESCENDING, null, null, null, 10)
	 *  </pre>
	 *  This will returns the newest 10 events, order by event date first, and then order by eventId
	 *  
	 *  suppose Event[] result is the returned events, event[0] is the newest one in the above example. 
	 *  	
	 *  The next step, you can query some older events.
     *  <pre>
     *   eventOperations().search(event[9].getDate(), event[9].getId(), Order.DESCENDING, null, null, null, 10)
     *  </pre>
	 *  
	 *  Or if you want to know if there are any new events created after your query last time,
     *  <pre>
     *   eventOperations().search(event[9].getDate(), event[0].getId(), Order.ASCENDING, null, null, null, 10)
     *  </pre>
	 * 
	 * Be noted: all other parameters, make sure you use the same parameters across all related query
	 * 
	 * @param dateToken the event date to be used as token, can be null (default to the current date)
	 * @param idToken the eventId  to be used as token for query, can be null
	 * @param order  how the result set should be ordered, can be null (default is Order.DESCENDING)
	 * @param subject key words of subject to search, can be null
	 * @param location the center geographical location where the returned events are near, can be null
	 * @param distance the distance (unit:killometer) within the center, can be null
	 * @param pageSize page size, can be null, default is 10
	 * @return an array of {@link Event} object
	 * @throws GrapeException if there is an error while communicating with Grape.
	 */
	
	Event[] search(Date dateToken, String idToken, Order order, String subject, GeoLocation location, Double distance, Integer pageSize);
	
    /**
     * get events started by myself.
     * @param pageNumber pages are zero indexed, 0 for {@code pageNumber} will return the first page.
     * @param pageSize how many items in one page
     * @return an array of {@link Event} object
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	Event[] getOwnedEvents(int pageNumber, int pageSize);
	
    /**
     * get events I joined.
     * @param pageNumber pages are zero indexed, 0 for {@code pageNumber} will return the first page.
     * @param pageSize how many items in one page
     * @return an array of {@link Event} object
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	Event[] getJoinedEvents(int pageNumber, int pageSize);
	
    /**
     * join me to an event. If you are the onwer of the event, you are already joined in, but
     * Event::memberIds won't include your userId.
     * @throws GrapeException if there is an error while communicating with Grape.
     */
	void joinEvent(String eventId);
		
	
    /**
     * leave me to an event. If you are the onwer of the event, you can not leave it, but
     * Event::memberIds won't include your userId.
     * @throws GrapeException if there is an error while communicating with Grape.
     */
    void leaveEvent(String eventId);
        
    	
	 /**
     * Create an event chat message
     * @param eventId the event the message to be sent to 
     * @param msg the chat message 
     * @return the message Id assigned by Grape 
     * @throws GrapeException if there is an error while communicating with Grape.
     */
    String addChatMessage(String eventId, ChatMessage msg);
	
    /**
     * query event message, only allow to query events I started or joined.
     * @param eventId the event the message to be sent to 
     * @param pageNumber pages are zero indexed, 0 for {@code pageNumber} will return the first page.
     * @param pageSize how many items in one page
     * @return an array of {@link ChatMessage} object
     * @throws GrapeException if there is an error while communicating with Grape.
     */
    ChatMessage[] getChatMessages(String eventId, int pageNumber, int pageSize);
    
    /**
     * Invite some people to join an event by SMS, email or Grape notification(for Grape users)
     * @param eventId the event Id
     * @param message the inviting message to send, can be null
     * @param contacts a list of Contact
     * @return void
     * @throws GrapeException if there is an error while communicating with Grape.
     * Example:
     *<pre>
     *    rest.eventOperations().invite("4f764b1c066a3a68f11820c7", "hi, this is the invitation message", contacts);
     *</pre>
     */
    void invite(String eventId, String message, List<Contact> contacts);
        
    /**
     * Set the poster picture for the event
     * @param eventId the event Id
     * @param pictureFilePath the local file path of the picture
     * @return void
     * @throws GrapeException if there is an error while communicating with Grape.
     * Example:
     * <pre>
     *    Event queryE=rest.eventOperations().getEvent("4f65832aca68199007d89784");
     *    rest.eventOperations().updatePosterPicture("4f65832aca68199007d89784", "c:\\picture.jpg");
     * </pre>
     */
    void updatePosterPicture(String eventId, String pictureFilePath);
    
    /**
     * get event's poster picture 
     * 
     * @param eventId the event Id
     * @param  output the output stream which this API will write binary data of the picture to
     * @throws IOException 
     * @throws GrapeException if there is an error while communicating with Grape.
     * Example:
     * <pre>
     *   FileOutputStream file;
     *   try 
     *   {
     *         file = new FileOutputStream("c:\\picture.jpg");
     *         rest.eventOperations().getPosterPicture("4f65832aca68199007d89784", file);
     *   } 
     *   catch (FileNotFoundException e) {
     *         e.printStackTrace();
     *  }
     * </pre>
     */
    void getPosterPicture(String eventId, OutputStream output) throws IOException;
    
}
