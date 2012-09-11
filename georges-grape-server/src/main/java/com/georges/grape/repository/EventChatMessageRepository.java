package com.georges.grape.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.georges.grape.data.ChatMessage;
import com.georges.grape.data.Event;

public interface EventChatMessageRepository extends PagingAndSortingRepository<ChatMessage, String>{
	List<ChatMessage> findByEventId(String eventId, Pageable pageable);
}
