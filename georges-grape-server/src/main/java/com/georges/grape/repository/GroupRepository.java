package com.georges.grape.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.georges.grape.data.Group;


public interface GroupRepository extends PagingAndSortingRepository<Group, String>{
	
	List<Group> findByNameLike(String name);
	List<Group> findByName(String name);

}
