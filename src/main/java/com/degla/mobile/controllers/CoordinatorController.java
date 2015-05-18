package com.degla.mobile.controllers;

import com.degla.db.models.ActorEO;
import com.degla.db.models.FileStates;

public class CoordinatorController implements CoordinatorOps {

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

	public Object[] scanCabinet(String cabinetId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * this method will create a new Cabinet
	 * @param cabinetId the cabinet id which will be created
	 */
	public Object[] createCabinet(String cabinetId) {
		throw new UnsupportedOperationException();
	}
}