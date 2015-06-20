package com.degla.db.models;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 20/06/15.
 */
@Entity(name="Transfer")
@Table(name="TBL_FILETRANSFER")
@DiscriminatorValue("fileTransfer")
public class Transfer extends EntityEO implements Serializable {

    @Index(name="fileNumberIdx")
    @Column(name="fileNumber")
    private String fileNumber;
    @Index(name="createdAtIndex")
    @Column(name="createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
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
    @Column(name="appointment_time")
    private String appointmentTime;

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}
