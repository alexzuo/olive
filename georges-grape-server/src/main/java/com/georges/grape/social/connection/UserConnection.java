package com.georges.grape.social.connection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.social.connect.ConnectionData;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "ids", def = "{'userId': 1, 'providerId': 1, 'providerUserId': 1}")
})
public class UserConnection {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String providerId;
    @Indexed
    private String providerUserId;
    private String displayName;
    private String profileUrl;
    private String imageUrl;
    private String accessToken;
    private String secret;
    private String refreshToken;
    private Long expireTime;
    private int rank;

    public UserConnection(String userId, String providerId, String providerUserId, String displayName, String profileUrl, String imageUrl, String accessToken,
            String secret, String refreshToken, Long expireTime, int rank) {
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.displayName = displayName;
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.accessToken = accessToken;
        this.secret = secret;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
        this.rank = rank;
    }

    public String getId()
    {
        return id;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUserId()
    {
        return userId;
    }

    /**
     * The id of the provider the connection is associated with.
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * The id of the provider user this connection is connected to.
     */
    public String getProviderUserId() {
        return providerUserId;
    }

    /**
     * A display name for the connection.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * A link to the provider's user profile page.
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * An image visualizing the connection.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * The access token required to make authorized API calls.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * The secret token needed to make authorized API calls. Required for
     * OAuth1-based connections.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * A token use to renew this connection. Optional. Always null for
     * OAuth1-based connections.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * The time the connection expires. Optional. Always null for OAuth1-based
     * connections.
     */
    public Long getExpireTime() {
        return expireTime;
    }

    public int getRank() {
        return rank;
    }

    public ConnectionData getConnectionData() {
        return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl,
                accessToken, secret, refreshToken, expireTime);

    }

}