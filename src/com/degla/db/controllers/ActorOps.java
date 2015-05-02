package com.degla.db.controllers;

import com.degla.db.models.Actor;
import com.degla.db.models.FileStates;

/**
 * This interface defines the operations that any actor in the system should possesses
 */
public interface ActorOps {

	/**
	 * @param username the username of the actor
	 * @param password the password of the employee
	 */
	public Actor login(String username, String password);

	/**
	 * @param file the patient file to be marked as missing
	 */
	public boolean markFile(Object file, FileStates state);
}