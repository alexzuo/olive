package com.georges.grape.data;

import java.util.Arrays;

public class GrapeUser {

    /**
     * Unique Id, read only. Server is responsible to set it.
     */
    private String id;
    /**
     * Display(screen) name. It is initially set according to the display name from QQ/Weibo.
     * User can change it, it will be stored in Grape server, so that it can be different from
     * the one in QQ/Weibo
     */
    private String displayName; 
    /**
     * Email address, default is null
     */
    private String email;
    /**
     * Age, default is 0 (unknown)
     */
    private int age;
    
    /**
     * User's avatar URI. Client application can not change this directly.
     * 
     * It is initially set according to user's avatar from QQ/Weibo, which has format
     * "http://...". Client application can retrieve it with this URL.
     * User can change it by uploading a new one, which only is stored in Grape server,
     * not synced with QQ/Weibo, and serve will reset it and its format is ObjectId 
     * string, for example:4f5c4c87b81080670818f446. Client application can retrieve it
     * with "http://<grape-server-URL>/api/user/avatar/4f5c4c87b81080670818f446.
     */
    private String avatarUri;
    
    /**
     * User's recent location(GPS coordinate, for example: (31.12434, 121.4211)). 
     * Client application can not change this directly.
     * 
     * There will be a way for application to update user's location in SDK.
     */
    private double[] location;

    @SuppressWarnings("unused")
    private GrapeUser()
    {
        
    }
   
    public GrapeUser(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public GrapeUser(String id, String displayName, String avatarUri, String email, int age, double[] location) {
        this.id = id;
        this.displayName = displayName;
        this.avatarUri = avatarUri;
        this.email = email;
        this.age = age;
        this.location = location;

    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getAvatarUri()  {
        return this.avatarUri;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public double[] getLocation() {
        return location;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "GrapeUser [id=" + id + ", displayName=" + displayName + ", avatarUri=" + avatarUri + ", email=" + email + ", age=" + age + ", location=" + Arrays
                .toString(location) + "]";
    }

}
