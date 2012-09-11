package com.georges.grape.social.connection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserSignature {
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String terminalId; 
    private Long expireTime;
    
    public UserSignature(String userId, String terminalId, String signature, Long expireTime) {
        super();
        this.userId = userId;
        this.terminalId = terminalId;
        this.signature = signature;
        this.expireTime = expireTime;
    }
    private String signature;
    
    
    public String getId()
    {
        return id;
    }
    public String getUserId() {
        return userId;
    }
    
    public String getTerminalId() {
        return terminalId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public Long getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
    @Override
    public String toString() {
        return "UserSignature [id=" + id + ", userId=" + userId + ", terminalId=" + terminalId + ", expireTime=" + expireTime + ", signature=" + signature + "]";
    }
    
}
