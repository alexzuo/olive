package com.georges.grape.api.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.georges.grape.api.EventOperations;
import com.georges.grape.data.ChatMessage;
import com.georges.grape.data.Contact;
import com.georges.grape.data.Event;
import com.georges.grape.data.GeoLocation;
import com.georges.grape.data.GrapeException;
import com.georges.grape.protocol.InvitationRequest;

class EventTemplate implements EventOperations {

	private final RestOperations restOperations;
	private final String EVENT_URL;
	
	public EventTemplate(String baseApiURL, RestOperations restOperations) {
		EVENT_URL=baseApiURL+"event/";
		this.restOperations = restOperations;
	}
	
	public Event getEvent(String eventId) {
		return restOperations.getForObject(EVENT_URL+eventId, Event.class);
	}
		
	public String createEvent(Event e) {
		return restOperations.postForObject(EVENT_URL+"event",e,String.class);
	}
	
	public void deleteEvent(String eventId) {
		restOperations.delete(EVENT_URL+eventId);
	}

           
	public Event[] search(
	        Date dateToken,
	        String idToken,
	        Order order, 
	        String subject, 
	        GeoLocation location, 
	        Double distance, 
	        Integer pageSize) {

		 URIBuilder builder= URIBuilder.fromUri(EVENT_URL+"events");
		 if(dateToken!=null)
		 {   //TODO: time zone
		     builder.queryParam("datetoken",String.valueOf(dateToken.getTime()));
		     System.out.println("search dateToken:"+dateToken.toString());
		 }
		 if(idToken!=null)
		     builder.queryParam("idtoken",idToken);
		 if(order!=null && order==Order.ASCENDING)
		     builder.queryParam("dir", String.valueOf(1));
		 else
		     builder.queryParam("dir", String.valueOf(-1));
		 if(pageSize!=null)
		     builder.queryParam("pagesize", String.valueOf(pageSize));
		 
		 if(subject!=null)
		     builder.queryParam("subject", subject);
		 if(location!=null)
		 {
		     builder.queryParam("lat", String.valueOf(location.getLatitude()));
		     builder.queryParam("lng", String.valueOf(location.getLongitude()));
		 }
		 if(distance!=null)		
			builder.queryParam("dis", String.valueOf(distance));
		 				
		 URI uri=builder.build(); 
		 return restOperations.getForObject(uri, Event[].class);
	}

	@Override
	public void updateEvent(Event e) {
		restOperations.put(EVENT_URL+e.getId(),e);
	}

    @Override
    public Event[] getOwnedEvents(int pageNumber, int pageSize) {
        URI uri = URIBuilder.fromUri(EVENT_URL+"ownedevents").queryParam("page", Integer.toString(pageNumber)).queryParam("size", Integer.toString(pageSize)).build();
        return restOperations.getForObject(uri, Event[].class);
    }

    @Override
    public Event[] getJoinedEvents(int pageNumber, int pageSize) {
        URI uri = URIBuilder.fromUri(EVENT_URL+"joinedevents").queryParam("page", Integer.toString(pageNumber)).queryParam("size", Integer.toString(pageSize)).build();
        return restOperations.getForObject(uri, Event[].class);
    }

    @Override
    public void joinEvent(String eventId) {
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/join").build();
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());
        restOperations.postForLocation(uri, null);
    }

    @Override
    public void leaveEvent(String eventId) {
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/leave").build();
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());
        restOperations.postForLocation(uri, null);
    }

    
    @Override
    public String addChatMessage(String eventId, ChatMessage msg) {
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/msg").build();
        return restOperations.postForObject(uri,msg, String.class);
    }

    @Override
    public ChatMessage[] getChatMessages(String eventId, int pageNumber, int pageSize) {
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/msg").queryParam("page", Integer.toString(pageNumber)).queryParam("size", Integer.toString(pageSize)).build();
        return restOperations.getForObject(uri,ChatMessage[].class);
    }
    @Override
    public void invite(String eventId, String message, List<Contact> contacts)
    {
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/invite").build();
        
        InvitationRequest request=new InvitationRequest(eventId, message, contacts);
          
        restOperations.postForLocation(uri,request );
    }

    @Override
    public void updatePosterPicture(String eventId, String pictureFilePath) {

        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/poster").build();
        
        FileSystemResource resource = new FileSystemResource(pictureFilePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
        form.add("file", resource);

        restOperations.postForLocation(uri, form);

    }

    @Override
    public void getPosterPicture(String eventId, OutputStream output) throws IOException {
     
        URI uri = URIBuilder.fromUri(EVENT_URL+eventId+"/poster").build();
        byte[] image=restOperations.getForObject(uri, byte[].class);
        if(image!=null)
        {
            output.write(image);
        }
        else
        {
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_POST_PICTURE);
        }
    }
	
}
