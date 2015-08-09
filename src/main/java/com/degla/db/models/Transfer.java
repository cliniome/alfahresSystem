package com.degla.db.models;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by snouto on 20/06/15.
 */
@Entity(name="Transfer")
@Table(name="TBL_FILETRANSFER")
@DiscriminatorValue("fileTransfer")
public class Transfer extends EntityEO implements Serializable , Comparable<Transfer> {

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

    @Column(name="appointment_date_g",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointment_Date_G;

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

    @Column(name="patientName",nullable = true)
    private String patientName;

    @Column(name="patientNumber",nullable = true)
    private String patientNumber;

    @Column(name="rmc_ord",nullable = true)
    private String rmc_ord;

    @Column(name="t_scheduleRuleNo",nullable = true)
    private String t_schedule_ruleNo;

    @Column(name="t_upd_user",nullable = true)
    private String t_upd_user;

    @Column(name="userName")
    @Index(name="req_userName")
    private String userName;

    @Column(name="cf_appointment_type",nullable = true)
    private String cf_appointment_type;

    @Column(name="CsGroupCount",nullable = true)
    private String csGroupCount;

    @Column(name="fileCurrentLocation")
    private String fileCurrentLocation;

    @Column(name="inpatient_col",nullable = false)
    private boolean inpatient=false;

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

    @Override
    public int compareTo(Transfer transfer) {

       return this.getAppointment_Date_G().compareTo(transfer.getAppointment_Date_G());

    }

    public boolean exactMatch(Transfer other) throws Exception
    {
        if(other.getAppointment_Hijri_Date() == null || other.getAppointmentTime() == null || other.getClinicCode() == null) throw new Exception("Transfer is incomplete");
        boolean result = other.getFileNumber().equals(this.getFileNumber()) && other.getAppointment_Hijri_Date().trim().equals(this.getAppointment_Hijri_Date().trim())
                && other.getClinicCode().trim().equals(this.getClinicCode().trim());

        return result;
    }

    public int getHourofAppointment()
    {
        String appHour = this.getAppointmentTime();

        if(appHour != null && appHour.contains(":"))
        {
            appHour = appHour.split(":")[0];

            return Integer.parseInt(appHour);


        }else return Integer.MAX_VALUE;
    }



    public Request toRequestObject()
    {
        Request request = new Request();

        request.setAppointment_Date(this.getAppointment_Date_G());
        request.setAppointment_date_h(this.getAppointment_Hijri_Date());
        request.setAppointment_made_by(this.getAppointment_Made_by());
        request.setAppointment_time(this.getAppointmentTime());
        request.setAppointment_Type(this.getAppointmentType());
        request.setBatchRequestNumber(this.getBatchRequestNumber());
        request.setCf_appointment_type(this.getCf_appointment_type());
        request.setClinic_Doc_Code(this.getClinicDocCode());
        request.setClinicCode(this.getClinicCode());
        request.setClinicName(this.getClinicName());
        request.setCsGroupCount(this.getCsGroupCount());
        request.setFileCurrentLocation(this.getFileCurrentLocation());
        request.setFileNumber(this.getFileNumber());
        request.setInpatient(this.isInpatient());
        request.setPatientName(this.getPatientName());
        request.setPatientNumber(this.getPatientNumber());
        request.setRequestingDocName(this.getClinicDocName());
        request.setRmc_ord(this.getRmc_ord());
        request.setT_schedule_ruleNo(this.getT_schedule_ruleNo());
        request.setT_upd_user(this.getT_upd_user());
        request.setUserName(this.getUserName());

        return request;
    }


    public FileHistory toFileHistory()
    {
        try
        {
            FileHistory history = new FileHistory();
            history.setAppointment_Hijri_Date(this.getAppointment_Hijri_Date());
            history.setAppointment_Made_by(this.getAppointment_Made_by());
            history.setAppointmentType(this.getAppointmentType());
            history.setBatchRequestNumber(this.getBatchRequestNumber());
            history.setClinicCode(this.getClinicCode());
            history.setClinicDocCode(this.getClinicDocCode());
            history.setClinicDocName(this.getClinicDocName());
            history.setClinicName(this.getClinicName());
            history.setCreatedAt(new Date());
            history.setState(FileStates.TRANSFERRED);
            history.setInpatient(this.isInpatient());

            return history;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public boolean isInpatient() {
        return inpatient;
    }

    public void setInpatient(boolean inpatient) {
        this.inpatient = inpatient;
    }



    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getRmc_ord() {
        return rmc_ord;
    }

    public void setRmc_ord(String rmc_ord) {
        this.rmc_ord = rmc_ord;
    }

    public String getT_schedule_ruleNo() {
        return t_schedule_ruleNo;
    }

    public void setT_schedule_ruleNo(String t_schedule_ruleNo) {
        this.t_schedule_ruleNo = t_schedule_ruleNo;
    }

    public String getT_upd_user() {
        return t_upd_user;
    }

    public void setT_upd_user(String t_upd_user) {
        this.t_upd_user = t_upd_user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCf_appointment_type() {
        return cf_appointment_type;
    }

    public void setCf_appointment_type(String cf_appointment_type) {
        this.cf_appointment_type = cf_appointment_type;
    }

    public String getCsGroupCount() {
        return csGroupCount;
    }

    public void setCsGroupCount(String csGroupCount) {
        this.csGroupCount = csGroupCount;
    }

    public String getFileCurrentLocation() {
        return fileCurrentLocation;
    }

    public void setFileCurrentLocation(String fileCurrentLocation) {
        this.fileCurrentLocation = fileCurrentLocation;
    }

    public Date getAppointment_Date_G() {
        return appointment_Date_G;
    }

    public void setAppointment_Date_G(Date appointment_Date_G) {
        this.appointment_Date_G = appointment_Date_G;
    }
}
