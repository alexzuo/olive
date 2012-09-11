package com.georges.grape.api.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

import com.georges.grape.api.EventOperations;
import com.georges.grape.api.Grape;
import com.georges.grape.api.UserOperations;

public class GrapeTemplate implements Grape {

    private UserOperations userOperations;
    private EventOperations eventOperations;

    private String baseApiURL;

    private String clientId;
    private String clientSecret;
    private String userId;
    private String userSignature;

    private RestTemplate restTemplate;

    //use this before sign in
    public GrapeTemplate(String baseApiURL, String clientId, String clientSecret) {
        this.baseApiURL = baseApiURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        initialize();
    }
    //use this after sign in
    public GrapeTemplate(String baseApiURL, String clientId, String clientSecret, String userId, String userSignature) {
        this.baseApiURL = baseApiURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userId = userId;
        this.userSignature = userSignature;

        initialize();
    }
    //use this to set the userId and signature
    public void signIn(String userId, String userSignature)
    {
        this.userId = userId;
        this.userSignature = userSignature;
        // Init again to use the userId and Signature
        initialize();
    }

    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(getFormMessageConverter());
        messageConverters.add(getJsonMessageConverter());
        messageConverters.add(getByteArrayMessageConverter());
        
        return messageConverters;
    }

    /**
     * Returns an {@link FormHttpMessageConverter} to be used by the internal
     * {@link RestTemplate}. By default, the message converter is set to use
     * "UTF-8" character encoding. Override to customize the message converter
     * (for example, to set supported media types or message converters for the
     * parts of a multipart message). To remove/replace this or any of the other
     * message converters that are registered by default, override the
     * getMessageConverters() method instead.
     */
    protected FormHttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter converter = new FormHttpMessageConverter();
        converter.setCharset(Charset.forName("UTF-8"));
        return converter;
    }

    /**
     * Returns a {@link MappingJacksonHttpMessageConverter} to be used by the
     * internal {@link RestTemplate}. Override to customize the message
     * converter (for example, to set a custom object mapper or supported media
     * types). To remove/replace this or any of the other message converters
     * that are registered by default, override the getMessageConverters()
     * method instead.
     */
    protected MappingJacksonHttpMessageConverter getJsonMessageConverter() {
                 
        return new MappingJacksonHttpMessageConverter();
    }

    /**
     * Returns a {@link ByteArrayHttpMessageConverter} to be used by the
     * internal {@link RestTemplate} when consuming image or other binary
     * resources. By default, the message converter supports "image/jpeg",
     * "image/gif", and "image/png" media types. Override to customize the
     * message converter (for example, to set supported media types). To
     * remove/replace this or any of the other message converters that are
     * registered by default, override the getMessageConverters() method
     * instead.
     */
    protected ByteArrayHttpMessageConverter getByteArrayMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG));
        return converter;
    }

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        restTemplate.setRequestFactory(null);
    }

    public UserOperations userOperations() {
        return userOperations;
    }

    public EventOperations eventOperations() {
        return eventOperations;
    }

    // private helpers
    private void initialize() {
        restTemplate = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
        restTemplate.setMessageConverters(getMessageConverters());
        restTemplate.setErrorHandler(new GrapeErrorHandler());

        restTemplate.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(new GrapeHttpRequestFactory(restTemplate.getRequestFactory(), clientId,
                this.clientSecret, this.userId, this.userSignature)));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(baseApiURL, restTemplate);
        eventOperations = new EventTemplate(baseApiURL, restTemplate);
    }

}
