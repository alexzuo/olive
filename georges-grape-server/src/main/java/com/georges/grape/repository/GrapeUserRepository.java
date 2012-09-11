package com.georges.grape.repository;

import java.util.List;

import org.springframework.data.mongodb.core.geo.Box;
import org.springframework.data.mongodb.core.geo.Circle;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.georges.grape.data.GrapeUser;


public interface GrapeUserRepository extends PagingAndSortingRepository<GrapeUser, String>{
	
	List<GrapeUser> findByAgeGreaterThan(int age);
	List<GrapeUser> findByAgeLessThan(int age);
	
	List<GrapeUser> findByAgeBetween(int from, int to);
	
	List<GrapeUser>	findByLocationNear(Point point);
	List<GrapeUser>	findByLocationWithin(Circle circle);
	List<GrapeUser>	findByLocationWithin(Box box);

	GrapeUser findById(String id);
}
