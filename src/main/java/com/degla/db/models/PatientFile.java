package com.degla.db.models;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.utils.FileStateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.*;

/**
 * this class represents the patient file which will be stored and retrieved from the system.
 */
@Entity(name="PatientFile")
@Table(name="TBL_PATIENTFILE")
@DiscriminatorValue("patientFile")
public class PatientFile extends EntityEO {


    @Index(name="fileIdIndex")
    @Column(name="fileID",unique = true)
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

    /*@ManyToOne(cascade={REFRESH,MERGE,DETACH,PERSIST},fetch=FetchType.EAGER,targetEntity=FileHistory.class)
    @JoinColumn(name="currentStatus_ID")*/
    @OneToOne(cascade = CascadeType.ALL)
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


    public boolean updateWithRestful(RestfulFile file)
    {
        this.setFileID(file.getFileNumber());
        this.setPatientName(file.getPatientName());
        this.setPatientNumber(this.getPatientNumber());
        if(file.getShelfId() != null && file.getShelfId().length() > 0)
            this.setShelfId(file.getShelfId());

        return true;
    }

    public RestfulFile toRestfulFile()
    {
        RestfulFile file = new RestfulFile();
        file.setCabinetId(this.getArchiveCabinet().getCabinetID());
        file.setDescription(this.getDescription());
        file.setFileNumber(this.getFileID());
        file.setShelfId(this.getShelfId());
        file.setState(this.getCurrentStatus().getState().toString());
        file.setTemporaryCabinetId(this.getCurrentStatus().getContainerId());
        file.setAppointmentDate(this.getCurrentStatus().getAppointment().getAppointment_Date().toString());
        file.setAppointmentDateH(this.getCurrentStatus().getAppointment().getAppointment_date_h());
        file.setAppointmentMadeBy(this.getCurrentStatus().getAppointment().getAppointment_made_by());
        file.setAppointmentTime(this.getCurrentStatus().getAppointment().getAppointment_time());
        file.setAppointmentType(this.getCurrentStatus().getAppointment().getAppointment_Type());
        file.setBatchRequestNumber(this.getCurrentStatus().getAppointment().getBatchRequestNumber());
        file.setClinicCode(this.getCurrentStatus().getAppointment().getClinicCode());
        file.setClinicDocCode(this.getCurrentStatus().getAppointment().getClinic_Doc_Code());
        file.setClinicDocName(this.getCurrentStatus().getAppointment().getRequestingDocName());
        file.setClinicName(this.getCurrentStatus().getAppointment().getClinicName());
        file.setPatientName(this.getPatientName());
        file.setPatientNumber(this.getPatientNumber());
        file.setState(this.getCurrentStatus().getState().toString());
        file.setInpatient(this.getCurrentStatus().getAppointment().isInpatient());
        file.setOperationDate(this.getCurrentStatus().getCreatedAt().getTime());


        return file;
    }
}