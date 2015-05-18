package com.degla.db.controllers;

import com.degla.db.models.ActorEO;
import com.degla.db.models.FileStates;

/**
 * This interface defines the operations that any actor in the system should possesses
 */
public interface ActorOps {


	/**
	 * @param file the patient file to be marked as missing
	 */
	public boolean markFile(Object file, FileStates state);
}