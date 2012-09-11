package com.georges.grape.controller;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.georges.grape.data.ChatMessage;
import com.georges.grape.data.Contact;
import com.georges.grape.data.Event;
import com.georges.grape.data.GrapeException;
import com.georges.grape.data.GrapeUser;
import com.georges.grape.protocol.InvitationRequest;
import com.georges.grape.repository.EventChatMessageRepository;
import com.georges.grape.repository.EventRepository;
import com.georges.grape.repository.GrapeUserRepository;
import com.georges.grape.service.NotificationService;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderStatus;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Controller
@RequestMapping("/api/event")
public class EventController extends BaseGrapeController {

    final int DEFAULT_PAGE_SIZE=10;
    @Autowired(required = true)
    EventRepository repository;
    @Autowired(required = true)
    EventChatMessageRepository msgRepository;
    @Autowired(required = true)
    NotificationService notificationService;
    @Autowired(required = true)
    GrapeUserRepository userRepository;
    
    Geocoder geocoder = new Geocoder();

    @RequestMapping(method = RequestMethod.GET, value = "/events")
    public @ResponseBody
    List<Event> queryEvents(@RequestParam(required=false,value="datetoken") Long dateToken,
                            @RequestParam(required=false,value="idtoken") String idToken,
                            @RequestParam(required=false,value="dir") Integer direction,
                            @RequestParam(required=false, value="subject") String subject, 
                            @RequestParam(required=false,value="lat") Double lat, 
                            @RequestParam(required=false,value="lng") Double lng, 
                            @RequestParam(required=false,value="dis") Double dis,
                            @RequestParam(required=false,value="pagesize") Integer pageSize)
	    throws GrapeException {

	mongoTemplate.indexOps(Event.class).ensureIndex(new GeospatialIndex("geolocation"));
	  
    Criteria cr = where("open").is(new Boolean(true));
    
	if(lat!=null && lng!=null)
	{
	    Point center = new Point(lng, lat);
	    cr=cr.and("geolocation").nearSphere(center);
	}
	if(dis!=null)
	{
	    cr=cr.maxDistance(dis/6378.137);
	}
    if(subject!=null)
    {
        cr = cr.and("subject").regex(subject);
    }
      
    Date startDate=null;
    if(dateToken!=null)
    {
        startDate =new Date(dateToken.longValue());
    }
    else
    {
        startDate=new Date();
    }

    //System.out.println("dateToken:"+startDate.toString());
    
    Order order=Order.DESCENDING;
    if(direction!=null &&direction.intValue()==1)
    {
        order=Order.ASCENDING;
    }
    
   // System.out.println("order:"+order.toString());
    
    
    
    if(order==Order.ASCENDING)
    {
        cr=cr.and("date").gte(startDate);
    }
    else
    {
        cr=cr.and("date").lte(startDate);
    }

 	Query q = new Query(cr);
 	q.sort().on("date", order).on("_id", order);

 	
 	if(pageSize!=null)
 	{
 	    int page=pageSize.intValue();
 	    if(idToken!=null)
 	        page+=1;
 	    
 	    q=q.limit(page);
 	    
 	}
 	else
 	    q=q.limit(DEFAULT_PAGE_SIZE);

 	List<Event> list=mongoTemplate.find(q,Event.class);
 	
 	//System.out.println(list.size());
 	
    if (idToken != null)
    {
        System.out.println("idToken:"+idToken);
        
        int index = -1;
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getId().equals(idToken))
            {
                index = i;
                break;
            }
        }
        if (index != -1)
        {
            return list.subList(index + 1, list.size());
        }
    }
 	 	
	return list;
	
   }

    @RequestMapping(method = RequestMethod.GET, value = "/ownedevents")
    public @ResponseBody
    List<Event> getMyOwnedEvents(@RequestParam(value="page") int pageNumber, @RequestParam(value="size") int pageSize, HttpServletRequest request) throws GrapeException {
        checkAuthentication(request);
        
        String userId = request.getHeader("userId"); 
        Page<Event> l= repository.findByOwnerId(userId, new PageRequest(pageNumber, pageSize));
        return l.getContent();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/joinedevents")
    public @ResponseBody
    List<Event> getJoinedEvents(@RequestParam(value="page") int pageNumber, @RequestParam(value="size") int pageSize, HttpServletRequest request) throws GrapeException {
        checkAuthentication(request);
        
        String userId = request.getHeader("userId"); 
        return repository.findByMemberIds(userId);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{eventId}/leave")
    public @ResponseBody void leave(@PathVariable String eventId, HttpServletRequest request)
        throws GrapeException {

        checkAuthentication(request);
        String userId = request.getHeader("userId"); 
        Event e=repository.findById(eventId);

        if(e!=null)
        {
            if(e.getOwnerId().equals(userId))
                return;

            String[] ids=e.getMemberIds();
            if(ids!=null)
            {
                Set<String> set = new HashSet<String>(Arrays.asList(ids));
                set.remove(userId);
                
                Field field = ReflectionUtils.findField(Event.class, "memberIds", String[].class);
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, e, set.toArray(new String[0]));
                repository.save(e);
            }
            
            return;
        }
       
        throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        
        
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{eventId}/join")
    public @ResponseBody void join(@PathVariable String eventId, HttpServletRequest request)
        throws GrapeException {
 
        System.out.println("in join EventId");
        checkAuthentication(request);
        String userId = request.getHeader("userId"); 
        Event e=repository.findOne(eventId);
                
        if(e!=null)
        {
            if(e.getOwnerId().equals(userId))
                return;
            
            if(!e.getOpen())
                throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
            
            String[] ids=e.getMemberIds();
            Set<String> newids=new HashSet<String>();
            boolean exist=false;
            if(ids!=null)
            {
                  
                for(int i=0;i<ids.length;i++)
                {  
                    //hack
                   if(ids[i]==null)
                   {   System.out.println("skip null id");
                       continue;
                   }
                    //already added
                    if(ids[i].equals(userId))
                    {
                        System.out.println("added already");
                        exist=true;
                    }
                    newids.add(ids[i]);
                }
               
               
            }
            newids.add(userId);
            
            Field field = ReflectionUtils.findField(Event.class, "memberIds", String[].class);
            ReflectionUtils.makeAccessible(field);
            System.out.println("setField");
            String[] newidarray=(String[])newids.toArray(new String[0]);
            System.out.println("try to set memberIds "+Arrays.toString(newidarray));
                       
            
            ReflectionUtils.setField(field, e, newidarray);
            System.out.println("setField after");
            
            repository.save(e);
            
            return;
        }
       
        throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);

    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/event")
    public @ResponseBody
    String createEvent(@RequestBody Event e, HttpServletRequest request) {
    checkAuthentication(request);
    
    String userId = request.getHeader("userId");     
    
    //set the geolocation
	GeocodeResponse response = geocoder.geocode(new GeocoderRequestBuilder().setAddress(e.getAddress()).setLanguage("zh").getGeocoderRequest());
	if (response.getStatus() == GeocoderStatus.OK) {
	    Field field = ReflectionUtils.findField(Event.class, "geolocation", double[].class);
	    ReflectionUtils.makeAccessible(field);
	    double[] loc = new double[2];
	    loc[0] = new Double(response.getResults().get(0).getGeometry().getLocation().getLng().doubleValue());
	    loc[1] = new Double(response.getResults().get(0).getGeometry().getLocation().getLat().doubleValue());
	    ReflectionUtils.setField(field, e, loc);
	}
	//set the ownerId
	Field field = ReflectionUtils.findField(Event.class, "ownerId", String.class);
	ReflectionUtils.makeAccessible(field);
	ReflectionUtils.setField(field, e, userId);
	
	//TODO:REMOVE later
/*	  field = ReflectionUtils.findField(Event.class, "memberIds", String[].class);
      ReflectionUtils.makeAccessible(field);
      String[] ids = new String[3];
      ids[0]=new String("111111");
      ids[1]=new String("QQ_ADECE0F88D1EB7DD8A852A9AD84B05A7");
      ids[2]=new String("3333333");
      ReflectionUtils.setField(field, e, ids);
	*/
	repository.save(e);
	return e.getId();
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{eventId}")
    public @ResponseBody
    Event getEvent(@PathVariable String eventId,  HttpServletRequest request) {
      
        return repository.findById(eventId);
        
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{eventId}")
    public @ResponseBody
    void updateEvent(@RequestBody Event m, @PathVariable String eventId, HttpServletRequest request) {
        
        checkAuthentication(request);
        String userId = request.getHeader("userId");   
   
        //check the eventId
        Event e=repository.findById(eventId);
        if(e==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        if((!e.getOwnerId().equals(userId)))
        {
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        }
        
        Field field = ReflectionUtils.findField(Event.class, "id", String.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, m, eventId);
         
        repository.save(m);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{eventId}")
    public @ResponseBody
    void removeEvent(@PathVariable String eventId,HttpServletRequest request) {
        checkAuthentication(request);
        String userId = request.getHeader("userId");   
   
        //check the eventId
        Event e=repository.findById(eventId);
        if(e==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        if((!e.getOwnerId().equals(userId)))
        {
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        }
        
        repository.delete(eventId);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{eventId}/msg")
    public @ResponseBody
    String addMessage(@PathVariable String eventId, 
                      @RequestBody ChatMessage m,
                      HttpServletRequest request) {
        
        checkAuthentication(request);
        String userId = request.getHeader("userId");     
        
        System.err.println(m.toString());
        
        //check the eventId
        Event e=repository.findById(eventId);
        if(e==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        if((!e.getOwnerId().equals(userId)) &&(! Arrays.asList(e.getMemberIds()).contains(userId)))
        {
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        }
        
        //set server generated fields
        Field field = ReflectionUtils.findField(ChatMessage.class, "userId", String.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, m, userId);
        
        field = ReflectionUtils.findField(ChatMessage.class, "eventId", String.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, m, eventId);
        
        Date now=new Date();
        field = ReflectionUtils.findField(ChatMessage.class, "date", Date.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, m, now);
        
        //reset id in case it exists
        field = ReflectionUtils.findField(ChatMessage.class, "id", String.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, m, null);
                
        return msgRepository.save(m).getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{eventId}/msg")
    public @ResponseBody
    List<ChatMessage> queryMessage(@PathVariable String eventId, 
                                   @RequestParam(value="page") int pageNumber, 
                                   @RequestParam(value="size") int pageSize, 
                                   HttpServletRequest request) {
        checkAuthentication(request);
        String userId = request.getHeader("userId");   
        
        //check the eventId
        Event e=repository.findById(eventId);
        if(e==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        if((!e.getOwnerId().equals(userId)) &&(! Arrays.asList(e.getMemberIds()).contains(userId)))
        {
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        }
        
        return msgRepository.findByEventId(eventId, new PageRequest(pageNumber, pageSize));
  
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/{eventId}/invite")
    public @ResponseBody
    void invite(@PathVariable String eventId, 
                @RequestBody InvitationRequest invite,
                HttpServletRequest request) {
        checkAuthentication(request);
        String userId = request.getHeader("userId");   
        GrapeUser user=userRepository.findById(userId);
        
        //check the eventId
        Event e=repository.findById(eventId);
        if(e==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        
        if((!e.getOwnerId().equals(userId)))
        {
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        }
        
        notificationService.invitePeopleJoinEvent(user.getDisplayName(),invite.getMessage(), e, invite.getContacts());
       
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{eventId}/poster")
    public @ResponseBody
    void setPosterPic(@PathVariable String eventId, 
                      MultipartHttpServletRequest request)
    {
        checkAuthentication(request);
        String userId = request.getHeader("userId");
        MultipartFile a = request.getFile("file");

        DB db = mongoTemplate.getDb();
        GridFS gfs = new GridFS(db, "eventPostPicture");

        Event event = repository.findById(eventId);
        if(event==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        if(!event.getOwnerId().equals(userId))
            throw new GrapeException(GrapeException.ErrorStatus.UNAUTHORIZED_ERROR);
        
        String oldPicUri = event.getPostPictureUri();

        // If the picture is not set, or it is from external, create a new one
        // in MongoDb
        GridFSInputFile in;
        try {
            in = gfs.createFile(a.getBytes());
            in.save();
            String picId = (String) in.getId().toString();
            System.err.println("picId: " + picId.toString());

            Field field = ReflectionUtils.findField(Event.class, "postPictureUri", String.class);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, event, picId);

            repository.save(event);
            if (oldPicUri != null && oldPicUri!="")
            {
                gfs.remove(new ObjectId(oldPicUri));
            }

        } catch (IOException e1) {
            // TODO: return GrapeException
            e1.printStackTrace();

        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/{eventId}/poster")
    public @ResponseBody
    void getPostPic(@PathVariable String eventId, HttpServletRequest request, HttpServletResponse response) {
        checkAuthentication(request);

        Event event = repository.findById(eventId);
        if(event==null)
            throw new GrapeException(GrapeException.ErrorStatus.NO_EVENT_ERROR);
        
        DB db = mongoTemplate.getDb();
        GridFS gfs = new GridFS(db, "eventPostPicture");
        GridFSDBFile file = gfs.find(new ObjectId(event.getPostPictureUri()));
        if(file==null)
        {   
            System.out.println("no pic for "+eventId);
            return;
        }
        
        response.setContentType("application/octet-stream"); 
        response.setContentLength(new Long(file.getLength()).intValue());
        response.setHeader("Content-Disposition","attachment; filename=file");
 
        try {
            FileCopyUtils.copy(file.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            throw new GrapeException(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR);
        }
        return;
                
    }
    
}
