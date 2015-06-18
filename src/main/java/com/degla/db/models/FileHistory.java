package com.degla.db.models;

import com.degla.utils.FileStateUtils;
import org.hibernate.annotations.Index;

import java.util.Date;


import javax.persistence.*;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;


/**
 * This class represents a file History and also the current status of a patient file
 */
@Entity(name="FileHistory")
@Table(name="TBL_FILEHISTORY")
@DiscriminatorValue("fileHistory")
public class FileHistory extends EntityEO {
	/**
	 * The primary key of the current file History
	 */
    @Index(name="createdAtIndex")
    @Column(name="createdAt")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Index(name="containerIdIndex")
    @Column(name="containerId")
	private String containerId;
    @Column(name="Batch_Request_No",nullable = true)
    private String batchRequestNumber;
    @Column(name="appointment_Hijri_Date",nullable = true)
    private String appointment_Hijri_Date;
    @Column(name="Appointment_Made_by",nullable = true)
    private String appointment_Made_by;
    @Column(name="AppointmentType",nullable = true)
    private String appointmentType;
    @Column(name="ClinicName",nullable = true)
    private String clinicName;
    @Column(name="clinicCode",nullable = true)
    private String clinicCode;
    @Column(name="ClinicDocName",nullable = true)
    private String clinicDocName;
    @Column(name="ClinicDocCode",nullable = true)
    private String clinicDocCode;
    //TODO : Don't forget to map PatientFile in this Class
    @ManyToOne
    @JoinColumn(name="patientFile")
	private PatientFile patientFile;
    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=Employee.class)
    @JoinColumn(name="empID")
	private Employee owner;
    @Index(name="StateIndex")
    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private FileStates state;

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	/**
	 * This attribute identifies the temporary cabinet IDs which will hold files during the entire workflow
	 *
	 * like temporary Cabinets , Distribution cabinets.....etc
	 */
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

    public Employee getOwner() {
        return owner;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public FileStates getState() {
        return state;
    }

    public void setState(FileStates state) {
        this.state = state;
    }

    public String getBatchRequestNumber() {
        return batchRequestNumber;
    }

    public void setBatchRequestNumber(String batchRequestNumber) {
        this.batchRequestNumber = batchRequestNumber;
    }

    public String getAppointment_Hijri_Date() {
        return appointment_Hijri_Date;
    }

    public void setAppointment_Hijri_Date(String appointment_Hijri_Date) {
        this.appointment_Hijri_Date = appointment_Hijri_Date;
    }

    public String getAppointment_Made_by() {
        return appointment_Made_by;
    }

    public void setAppointment_Made_by(String appointment_Made_by) {
        this.appointment_Made_by = appointment_Made_by;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getClinicDocName() {
        return clinicDocName;
    }

    public void setClinicDocName(String clinicDocName) {
        this.clinicDocName = clinicDocName;
    }

    public String getClinicDocCode() {
        return clinicDocCode;
    }

    public void setClinicDocCode(String clinicDocCode) {
        this.clinicDocCode = clinicDocCode;
    }

    public String getReadableState()
    {
        FileStateUtils utils = new FileStateUtils(getState());
        return utils.getReadableState();
    }
}