package com.degla.db.models;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.*;

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

    @ManyToOne(cascade={REFRESH,MERGE,DETACH,PERSIST},fetch=FetchType.EAGER,targetEntity=ArchiveCabinet.class)
    @JoinColumn(name="Ar_cabinetID")
	private ArchiveCabinet archiveCabinet;

    @ManyToOne(cascade={REFRESH,MERGE,DETACH,PERSIST},fetch=FetchType.EAGER,targetEntity=FileHistory.class)
    @JoinColumn(name="currentStatus_ID")
	private FileHistory currentStatus;

   /* @OneToMany(cascade = {REFRESH,MERGE,DETACH,PERSIST},orphanRemoval = true,fetch = FetchType.EAGER,mappedBy = "patientFile")
    @OrderBy("createdAt desc")
    private List<FileHistory> histories;*/

    @Column(name="ShelfId",nullable = true)
    private String shelfId;

    @Column(name="patientNumber",nullable = true)
    private String patientNumber;

    @Column(name="Patient_Name",nullable = true)
    private String patientName;


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


    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public RestfulFile toRestfulFile()
    {
        RestfulFile file = new RestfulFile();
        file.setCabinetId(this.getArchiveCabinet().getCabinetID());
        file.setDescription(this.getDescription());
        file.setFileNumber(this.getFileID());
        file.setShelfId(this.getShelfId());
        file.setState(FileModelStates.valueOf(this.getCurrentStatus().getState().toString()));
        file.setTemporaryCabinetId(this.getCurrentStatus().getContainerId());

        return file;
    }
}