package com.degla.db.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by snouto on 15/05/15.
 */
@Entity
@Table(name="TBL_REQUESTS")
@DiscriminatorValue("Request")
public class Request implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @Column(name="PatientNumber")
    private String patientNumber;

    @Column(name="FileNumber")
    private String fileNumber;

    @Column(name="PatientName")
    private String patientName;

    @Column(name="appointment_Type")
    private String appointment_Type;

    @Column(name="appointment_Date")
    private String appointment_Date;

    @Column(name="userName")
    private String userName;

    @Column(name="fileCurrentLocation")
    private String fileCurrentLocation;

    @Column(name="ClinicCode")
    private String clinicCode;

    @Column(name="clinic_Doc_Code")
    private String clinic_Doc_Code;



    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getAppointment_Type() {
        return appointment_Type;
    }

    public void setAppointment_Type(String appointment_Type) {
        this.appointment_Type = appointment_Type;
    }

    public String getAppointment_Date() {
        return appointment_Date;
    }

    public void setAppointment_Date(String appointment_Date) {
        this.appointment_Date = appointment_Date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileCurrentLocation() {
        return fileCurrentLocation;
    }

    public void setFileCurrentLocation(String fileCurrentLocation) {
        this.fileCurrentLocation = fileCurrentLocation;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getClinic_Doc_Code() {
        return clinic_Doc_Code;
    }

    public void setClinic_Doc_Code(String clinic_Doc_Code) {
        this.clinic_Doc_Code = clinic_Doc_Code;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
