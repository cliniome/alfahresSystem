package com.degla.mobile.controllers;

import com.degla.db.models.ActorEO;
import com.degla.db.models.FileStates;

public class KeeperController implements KeeperOps {

	/**
	 * @param username the username of the actor
	 * @param password the password of the employee
	 */
	public ActorEO login(String username, String password) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param file the patient file to be marked as missing
	 */
	public boolean markFile(Object file, FileStates state) {
		throw new UnsupportedOperationException();
	}

	/**
	 * retrieves all the assigned files by the receptionist
	 */
	public Object[] retrieveFiles() {
		throw new UnsupportedOperationException();
	}
}