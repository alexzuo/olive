package com.georges.grape.api;

/**
 * Interface specifying a basic set of operations for interacting with Grap server.
 * Implemented by {@link com.georges.grape.api.impl.GrapeTemplate}.
 * @author Alex Zuo
 */
public interface Grape{

	/**
	 * API for performing operations on events.
	 */
	EventOperations eventOperations();
	
	/**
	 * API for performing operations on users.
	 */
	UserOperations userOperations();
	


}
