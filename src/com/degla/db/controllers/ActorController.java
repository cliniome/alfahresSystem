package com.degla.db.controllers;

import com.degla.db.models.Actor;
import com.degla.db.models.FileStates;

public class ActorController implements ReceptionistOps {

	/**
	 * @param username the username of the actor
	 * @param password the password of the employee
	 */
	public Actor login(String username, String password) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param file the patient file to be marked as missing
	 */
	public boolean markFile(Object file, FileStates state) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param fileLocation it takes a file location and processes it after it has been uploaded by the UI and stored onto the server file system
	 */
	public boolean distributeAppointments(String fileLocation) {
		throw new UnsupportedOperationException();
	}
}