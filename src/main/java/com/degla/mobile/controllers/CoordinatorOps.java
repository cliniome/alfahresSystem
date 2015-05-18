package com.degla.mobile.controllers;

import com.degla.db.controllers.ActorOps;

public interface CoordinatorOps extends ActorOps {

	public Object[] scanCabinet(String cabinetId);

	/**
	 * this method will create a new Cabinet
	 * @param cabinetId the cabinet id which will be created
	 */
	public Object[] createCabinet(String cabinetId);
}