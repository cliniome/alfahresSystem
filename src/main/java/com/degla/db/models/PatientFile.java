package com.degla.db.models;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

/**
 * this class represents the patient file which will be stored and retrieved from the system.
 */
@Entity
@Table(name="TBL_PATIENTFILE")
@DiscriminatorValue("patientFile")
public class PatientFile extends EntityEO {


    @Index(name="fileIdIndex")
    @Column(name="fileID")
	private String fileID;
    @Column(name="description",nullable = true)
	private String description;

    @Index(name="creationTimeIndex")
    @Column(name="creationTime")
    @Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=ArchiveCabinet.class)
    @JoinColumn(name="Ar_cabinetID")
	private ArchiveCabinet archiveCabinet;

    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=FileHistory.class)
    @JoinColumn(name="currentStatus_ID")
	private FileHistory currentStatus;

    @OneToMany(cascade = {REFRESH,MERGE,DETACH},fetch = FetchType.LAZY,mappedBy = "patientFile")
    private List<FileHistory> histories;

    @Column(name="ShelfId",nullable = true)
    private String shelfId;


	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	/**
	 * this is the file id that identifies a given file
	 */
    public String getFileID() {
		return this.fileID;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * A simple description of the file
	 */
    public String getDescription() {
		return this.description;
	}


    /**
     * This property identifies when the file has been first created in the system and loaded into an archive cabinet
     */
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public ArchiveCabinet getArchiveCabinet() {
        return archiveCabinet;
    }

    public void setArchiveCabinet(ArchiveCabinet archiveCabinet) {
        this.archiveCabinet = archiveCabinet;
    }

    public FileHistory getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(FileHistory currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<FileHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<FileHistory> histories) {
        this.histories = histories;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }
}