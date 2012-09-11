package com.georges.grape.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.georges.grape.data.Event;

public interface EventRepository extends PagingAndSortingRepository<Event, String>{
	
	List<Event> findBySubjectLike(String name);
	List<Event> findBySubject(String name);
	Event findById(String id);
	Page<Event> findByOwnerId(String ownerId, Pageable pageable);
	List<Event> findByMemberIds(String userId);
	GeoResults<Event> findByGeolocationNearAndSubjectLike(Point location, Distance distance,String subject);
	
}
