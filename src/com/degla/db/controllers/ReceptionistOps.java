package com.degla.db.controllers;

/**
 * This interface defines operations must be taken by an employee in the system
 */
public interface ReceptionistOps extends ActorOps {

	/**
	 * @param fileLocation it takes a file location and processes it after it has been uploaded by the UI and stored onto the server file system
	 */
	public boolean distributeAppointments(String fileLocation);
}