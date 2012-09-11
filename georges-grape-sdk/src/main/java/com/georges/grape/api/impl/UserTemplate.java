package com.georges.grape.api.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.georges.grape.api.UserOperations;
import com.georges.grape.data.GrapeUser;

class UserTemplate implements UserOperations {

    private final RestOperations restOperations;
    private final String USER_URL;

    public UserTemplate(String baseApiURL, RestOperations restOperations) {
        USER_URL = baseApiURL + "user/";
        this.restOperations = restOperations;

    }

    public GrapeUser getMyProfile() {
        return restOperations.getForObject(USER_URL + "me", GrapeUser.class);
    }

    public GrapeUser getUserProfile(String userId) {
        return restOperations.getForObject(USER_URL + "user/" + userId, GrapeUser.class);
    }

    public void updateMyProfile(GrapeUser user) {
        restOperations.put(USER_URL + "me", user);
    }

    public String updateMyAvatar(String pictureFilePath)
    {
        FileSystemResource resource = new FileSystemResource(pictureFilePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
        form.add("file", resource);

        return restOperations.postForObject(USER_URL + "avatar", form, String.class);

    }

    public void getAvatar(String avatarUri, OutputStream output)
    {
        if(avatarUri==null)
            return;
        
        String url=null;
        if(avatarUri.startsWith("http"))
            url=avatarUri;
        else
            url=USER_URL+"avatar/"+avatarUri;
        
        byte[] image=restOperations.getForObject(url, byte[].class);
      
        try {
            output.write(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }

}
