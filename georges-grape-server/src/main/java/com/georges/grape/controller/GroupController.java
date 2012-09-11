package com.georges.grape.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.georges.grape.data.Group;
import com.georges.grape.repository.GroupRepository;


@Controller
@RequestMapping("/api/group")
public class GroupController  extends BaseGrapeController{

	@Autowired(required = true)
	GroupRepository repository;
	
	@RequestMapping(value = "/xsecured", method = RequestMethod.GET)
	public @ResponseBody String secured() {
		return "XSECURED in olive: " + new Date();
	}
		
	
	@RequestMapping(method=RequestMethod.GET, value="/groups")
	public @ResponseBody List<Group> getAllGroups() {
		List<Group> groups =new ArrayList<Group>();
		
		Iterator<Group> p=repository.findAll().iterator();
	 	
	 	while(p.hasNext())
	 	{
	 		groups.add(p.next());
	 	}
		
		return groups;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/group")
	public @ResponseBody Group addGroup(@RequestBody Group g) {
		repository.save(g);
		return g;
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/group/{id}")
	public @ResponseBody Group updateGroup(@RequestBody Group g, @PathVariable String id) {
		repository.save(g);
		return g;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/group/{id}")
	public @ResponseBody void removeGroup(@PathVariable String id) {
		repository.delete(id);
	}
}
