package com.georges.grape.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.georges.social.qq.api.QQ;
import com.georges.social.qq.api.impl.QQTemplate;
import com.georges.social.weibo.api.Weibo;
import com.georges.social.weibo.api.impl.WeiboTemplate;


@Configuration
/*@ImportResource(
	   {"classpath:com/georges/grape/config/spring-jdbc.xml",
		"classpath:com/georges/grape/config/spring-mongodb.xml",
		"classpath:com/georges/grape/config/spring-social.xml",
		"classpath:com/georges/grape/config/spring-rest.xml",
		"classpath:com/georges/grape/config/spring-security.xml"})*/
public class MainConfig {
	
	@Autowired
	UsersConnectionRepository usersConnectionRepository;
	
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public ConnectionRepository connectionRepository() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
		}
		return usersConnectionRepository.createConnectionRepository(authentication.getName());
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Facebook facebook() {
		Connection<Facebook> facebook =connectionRepository().findPrimaryConnection(Facebook.class);
		return facebook != null ? facebook.getApi() : new FacebookTemplate();
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Twitter twitter() {
		Connection<Twitter> twitter = connectionRepository().findPrimaryConnection(Twitter.class);
		return twitter != null ? twitter.getApi() : new TwitterTemplate();
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public QQ qq() {
		Connection<QQ> qq = connectionRepository().findPrimaryConnection(QQ.class);
		return qq != null ? qq.getApi() : new QQTemplate();
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Weibo weibo() {
		Connection<Weibo> weibo = connectionRepository().findPrimaryConnection(Weibo.class);
		return weibo != null ? weibo.getApi() : new WeiboTemplate();
	}

}