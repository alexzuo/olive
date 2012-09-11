package com.georges.grape.controller;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.georges.grape.data.GrapeException;
import com.georges.grape.social.connection.UserSignature;

@Controller
public class BaseGrapeController {

    @Autowired(required = true)
    protected MongoTemplate mongoTemplate;
    
	@ExceptionHandler (GrapeException.class)
	public @ResponseBody String handleAllExceptions(GrapeException ex, HttpServletResponse response ) {
		System.err.println("handle All exceptions:"+ex);
		
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		Map<String, Object> err=new HashMap<String, Object>();
		err.put("errorCode", ex.getErrorStatus().value());
		err.put("detailMessage",ex.getMessage());
		
		response.setStatus( ex.getErrorStatus().httpstatus());
		
		return mapper.valueToTree(err).toString();
	}
	
	protected void checkAuthentication(HttpServletRequest request) {
	    // TODO Auto-generated method stub
	    String clientId=request.getHeader("clientId");
	    String clientSecret=request.getHeader("clientSecret");
	    String userId=request.getHeader("userId");    
	    String userSignature= request.getHeader("userSignature");
	
	    //TODO check clientId and secret
	    
	    Query q=new Query(where("userId").is(userId).and("signature").is(userSignature));
	    
	    List<UserSignature> list= mongoTemplate.find(q, UserSignature.class);
	    if(list.size()==0)
	    {
	        throw new GrapeException(GrapeException.ErrorStatus.INVALID_SIGNAGURE_ERROR);
	    }
	    
	}
}
