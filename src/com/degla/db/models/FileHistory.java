package com.degla.db.models;

/**
 * This class represents a file History and also the current status of a patient file
 */
public class FileHistory {
	/**
	 * The primary key of the current file History
	 */
	private String id;
	/**
	 * The createdAt timestamp , showing when this step has been created for this file
	 */
	private Timestamp createdAt;
	/**
	 * This attribute identifies the temporary cabinet IDs which will hold files during the entire workflow
	 * 
	 * like temporary Cabinets , Distribution cabinets.....etc
	 */
	private String containerId;
	private PatientFile patientFile;
	private Employee owner;
	public FileStates state;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerId() {
		return this.containerId;
	}
}