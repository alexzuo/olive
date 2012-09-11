package com.georges.grape.social.connection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import com.georges.grape.data.GrapeUser;
import com.georges.grape.repository.GrapeUserRepository;

public class UserConnectionImplicitSignUp implements ConnectionSignUp {

	@Autowired
	private GrapeUserRepository accountRepository;

	/**
	 * Sign up a new user of the application from the connection.
	 * @param connection the connection
	 * @return the new user id. May be null to indicate that an implicit local user profile could not be created.
	 */
	public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        GrapeUser user=new GrapeUser(profile.getUsername(), profile.getName(), connection.getImageUrl(), profile.getEmail(), 0, null);
        accountRepository.save(user);

        return user.getId();
    }
	
}