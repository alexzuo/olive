package com.georges.grape.social.connection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.georges.grape.data.GrapeUser;
import com.georges.grape.repository.GrapeUserRepository;

public class MongoUserDetailsService implements UserDetailsService {

	@Autowired
	private GrapeUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		GrapeUser user=userRepository.findById(userName);
		if(user!=null)
		{
		    Collection<GrantedAuthority> authorities = 
	        new HashSet<GrantedAuthority>();
	        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	        
	        return new User(user.getId(),"password",authorities); 
		}
		
		return null;
	}

}
