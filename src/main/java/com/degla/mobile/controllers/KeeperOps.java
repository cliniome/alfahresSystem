package com.degla.mobile.controllers;

import com.degla.db.controllers.ActorOps;

public interface KeeperOps extends ActorOps {

	/**
	 * retrieves all the assigned files by the receptionist
	 */
	public Object[] retrieveFiles();
}