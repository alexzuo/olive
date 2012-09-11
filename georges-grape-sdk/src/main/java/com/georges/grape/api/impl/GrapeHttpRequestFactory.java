package com.georges.grape.api.impl;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

public class GrapeHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {
	String clientId;
	String clientScret;
	String userName;
	String userSignature;
    		 
    public GrapeHttpRequestFactory(ClientHttpRequestFactory requestFactory, String clientId, String clientScret, String userName, String userSignature) {
	super(requestFactory);
	
	 this.clientId=clientId;
	 this.clientScret=clientScret;
	 this.userName=userName;
	 this.userSignature=userSignature;
    }

    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) throws IOException {
	ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
		
	request.getHeaders().set("clientId", clientId);
	request.getHeaders().set("clientScret", clientScret);
	
	if(userName!=null && userSignature!=null) {
    	request.getHeaders().set("userId", userName);
    	request.getHeaders().set("userSignature", userSignature);
	}
	
	return request;
    }

}
