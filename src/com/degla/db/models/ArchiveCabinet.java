package com.degla.db.models;

/**
 * This cabinet represents an archive cabinet which will contain archived_files in the Enterprise's Archive room,
 * Each cabinet will be assigned to a given employee
 */
public class ArchiveCabinet {
	/**
	 * this is the cabinet number identifier
	 */
	private String id;
	/**
	 * this is the location of the cabinet
	 */
	private String location;
	/**
	 * this is a simple description of the cabinet
	 */
	private String description;
	/**
	 * This is a timestamp showing when this cabinet has been created into the system.
	 */
	private Timestamp creationTime;
	private Employee assignedTo;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return this.location;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public Timestamp getCreationTime() {
		return this.creationTime;
	}
}