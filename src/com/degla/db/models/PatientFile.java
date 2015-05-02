package com.degla.db.models;

/**
 * this class represents the patient file which will be stored and retrieved from the system.
 */
public class PatientFile {
	/**
	 * this is the file id that identifies a given file
	 */
	private String fileID;
	/**
	 * A simple description of the file
	 */
	private String description;
	/**
	 * This property identifies when the file has been first created in the system and loaded into an archive cabinet
	 */
	private Timestamp creationTime;
	private ArchiveCabinet archiveCabinet;
	public FileHistory currentStatus;

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getFileID() {
		return this.fileID;
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