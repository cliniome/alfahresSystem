package com.degla.db.models;

import java.util.Date;


import javax.persistence.*;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;


/**
 * This class represents a file History and also the current status of a patient file
 */
@Entity
@Table(name="TBL_FILEHISTORY")
@DiscriminatorValue("fileHistory")
public class FileHistory extends EntityEO {
	/**
	 * The primary key of the current file History
	 */
    @Column(name="createdAt")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	/**
	 * This attribute identifies the temporary cabinet IDs which will hold files during the entire workflow
	 * 
	 * like temporary Cabinets , Distribution cabinets.....etc
	 */
    @Column(name="containerId")
	private String containerId;
    //TODO : Don't forget to map PatientFile in this Class
    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=PatientFile.class)
    @JoinColumn(name="patientFile")
	private PatientFile patientFile;
    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=Employee.class)
    @JoinColumn(name="empID")
	private Employee owner;
    @Column(name="state")
    @Enumerated(EnumType.STRING)
	public FileStates state;

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerId() {
		return this.containerId;
	}

    /**
     * The createdAt timestamp , showing when this step has been created for this file
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public PatientFile getPatientFile() {
        return patientFile;
    }

    public void setPatientFile(PatientFile patientFile) {
        this.patientFile = patientFile;
    }
}