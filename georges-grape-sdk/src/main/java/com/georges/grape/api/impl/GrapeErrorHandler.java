package com.georges.grape.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.georges.grape.data.GrapeException;


/**
 * Subclass of {@link DefaultResponseErrorHandler} that handles errors from Grape API
 * interpreting them into <code>GrapeException</code>
 * 
 * @author Alex Zuo
 */
class GrapeErrorHandler extends DefaultResponseErrorHandler  {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException{
		System.err.println("Status code:"+response.getStatusCode());

		//if server gets an invalid token, it will return 401 with message boby 
		//"{"error":"invalid_token","error_description":"Invalid access token: sometoken"}"
		//but seems Spring can not get the body message when authenticated is needed.
		//it will throw "I/O error: cannot retry due to server authentication, in streaming mode"
		//we have no way to know it is because token expires or token invalid
		String body=null;
		try 
		{
			body = readFully(response.getBody());
		}
		catch(IOException e)
		{
			//ignore
		}
		
		if(response.getStatusCode()==HttpStatus.UNAUTHORIZED)
		{
			handleGrapeException(body, response);			
		}
		else if(response.getStatusCode().series() ==HttpStatus.Series.REDIRECTION)
		{	
			handleRedirectException(body, response);
	
		}
		handleGrapeException(body, response);
	}
	
	@Override 
	public boolean hasError(ClientHttpResponse response) throws IOException {

		if(response.getStatusCode().series() ==HttpStatus.Series.REDIRECTION)
		{
			return true;
		}
		
		if(super.hasError(response)) {
			return true;
		}

		return false;
	}
	
	void handleRedirectException(String bodyMessage, ClientHttpResponse response) {
		
		//Server will redirect to login page if not authorized
		String location=response.getHeaders().getLocation().toString();
		if(location.contains("login"))
		{
			throw new GrapeException(GrapeException.ErrorStatus.UNAUTHENTICATED_ERROR, "non authenticated user, please login");
		}
		else
		{
			handleUncategorizedException(bodyMessage, response);
		}
	}
	
	
	void handleGrapeException(String bodyMessage, ClientHttpResponse response) throws IOException {
		GrapeException x= convertMessage(bodyMessage);
		if(x==null)
		{
			handleUncategorizedException(bodyMessage, response);
		}
		throw x;
	}


	private void handleUncategorizedException(String bodyMessage,ClientHttpResponse response ) {
		throw new GrapeException(GrapeException.ErrorStatus.INTERANL_SERVER_ERROR, "can not parse error message returned from server: "+bodyMessage);
	}

    @SuppressWarnings("unchecked")
	private GrapeException convertMessage(String bodyMessage) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
						
		try {
		    Map<String, Object> responseMap = mapper.<Map<String, Object>>readValue(bodyMessage, new TypeReference<Map<String, Object>>() {});
		    if (responseMap.containsKey("errorCode")) {
		    	return new GrapeException( GrapeException.ErrorStatus.valueOf((Integer)responseMap.get("errorCode")), (String)responseMap.get("detailMessage"));
		    }
		    //Spring Security will return its own message like this:
		    //{"error":"invalid_token","error_description":"Invalid access token: accessToken"}]
		    else if(responseMap.containsKey("error"))
    		{
		    	String error=(String)responseMap.get("error");
		    	String desc=(String)responseMap.get("error_description");
		    	
		    	if(error.equals("invalid_token"))
		    	{
		    		return new GrapeException(GrapeException.ErrorStatus.INVALID_TOKEN_ERROR, desc);
		    	}
		    	    	
    		}
		} catch (JsonParseException e) {
			return null;
		}
		return null;		
	}
	
	private String readFully(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		while (reader.ready()) {
			sb.append(reader.readLine());
		}
		return sb.toString();
	}
}
