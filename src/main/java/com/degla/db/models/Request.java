package com.degla.db.models;

import com.degla.restful.models.RestfulRequest;
import com.degla.utils.AnnotatingModel;
import org.hibernate.annotations.Index;
import static javax.persistence.CascadeType.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 15/05/15.
 */
@Entity(name="Request")
@Table(name="TBL_REQUESTS")
@DiscriminatorValue("Request")
public class Request implements Serializable, AnnotatingModel, Comparable<Request> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @Column(name="PatientNumber")
    @Index(name="req_patientNumber")
    private String patientNumber;

    @Column(name="FileNumber",unique = true)
    @Index(name="req_fileNumber")
    private String fileNumber;

    @Column(name="PatientName")
    @Index(name="req_patientName")
    private String patientName;

    @Column(name="appointment_Type")
    private String appointment_Type;

    @Column(name="app_made_by",nullable = true)
    private String appointment_made_by;

    @Column(name="appointment_date_h",nullable = true)
    private String appointment_date_h;

    @Column(name="rmc_ord",nullable = true)
    private String rmc_ord;

    @Column(name="appointment_time",nullable = true)
    private String appointment_time;

    @Column(name="t_scheduleRuleNo",nullable = true)
    private String t_schedule_ruleNo;

    @Column(name="appointment_Date")
    private String appointment_Date;

    @Column(name="t_upd_user",nullable = true)
    private String t_upd_user;

    @Column(name="userName")
    @Index(name="req_userName")
    private String userName;

    @Column(name="fileCurrentLocation")
    private String fileCurrentLocation;

    @Column(name="cf_appointment_type",nullable = true)
    private String cf_appointment_type;


    @Column(name="ClinicName",nullable = true)
    private String clinicName;

    @Column(name="requestingDocName",nullable = true)
    private String requestingDocName;

    @Column(name="ClinicCode")
    private String clinicCode;

    @Column(name="clinic_Doc_Code")
    private String clinic_Doc_Code;

    @ManyToOne(cascade = {MERGE,REFRESH,DETACH})
    @JoinColumn(name="assigned_To",nullable = true)
    private Employee assignedTo;

    @Column(name = "Batch_Request_Number",nullable = true)
    private String batchRequestNumber;

    @Column(name="CsGroupCount",nullable = true)
    private String csGroupCount;

    @Column(name="inpatient_Col",nullable = false)
    private boolean inpatient = false;

    private transient boolean selected;


    public Transfer toTransferObject()
    {
        try
        {
            Transfer currentTransfer = new Transfer();
            currentTransfer.setAppointment_Hijri_Date(this.getAppointment_date_h());
            currentTransfer.setAppointment_Made_by(this.getAppointment_made_by());
            currentTransfer.setAppointmentType(this.getAppointment_Type());
            currentTransfer.setBatchRequestNumber(this.getBatchRequestNumber());
            currentTransfer.setClinicCode(this.getClinicCode());
            currentTransfer.setClinicDocCode(this.getClinic_Doc_Code());
            currentTransfer.setClinicDocName(this.getRequestingDocName());
            currentTransfer.setClinicName(this.getClinicName());
            currentTransfer.setCreatedAt(new Date());
            currentTransfer.setFileNumber(this.getFileNumber());
            currentTransfer.setAppointmentTime(this.getAppointment_time());
            return currentTransfer;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public Request clone() {

        Request newRequest = new Request();
        newRequest.setAppointment_Date(this.getAppointment_Date());
        newRequest.setAppointment_Type(this.getAppointment_Type());
        newRequest.setClinic_Doc_Code(this.getClinic_Doc_Code());
        newRequest.setClinicCode(this.getClinicCode());
        newRequest.setFileCurrentLocation(this.getFileCurrentLocation());
        newRequest.setFileNumber(this.getFileNumber());
        newRequest.setId(this.getId());
        newRequest.setPatientName(this.getPatientName());
        newRequest.setPatientNumber(this.getPatientNumber());
        newRequest.setUserName(this.getUserName());
        newRequest.setBatchRequestNumber(this.getBatchRequestNumber());
        newRequest.setCsGroupCount(this.getCsGroupCount());
        newRequest.setAppointment_made_by(this.getAppointment_made_by());
        newRequest.setAppointment_date_h(this.getAppointment_date_h());
        newRequest.setCf_appointment_type(this.getCf_appointment_type());
        newRequest.setRequestingDocName(this.getRequestingDocName());
        newRequest.setClinicName(this.getClinicName());
        newRequest.setRmc_ord(this.getRmc_ord());
        newRequest.setAppointment_time(this.getAppointment_time());
        newRequest.setT_schedule_ruleNo(this.getT_schedule_ruleNo());
        newRequest.setT_upd_user(this.getT_upd_user());

        return newRequest;
    }



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

    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }


    @Override
    public String toString() {

        return this.getFileNumber();
    }

    @Override
    public Object getRowKey() {
        return this.getFileNumber();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getBatchRequestNumber() {
        return batchRequestNumber;
    }

    public void setBatchRequestNumber(String batchRequestNumber) {
        this.batchRequestNumber = batchRequestNumber;
    }

    public String getCsGroupCount() {
        return csGroupCount;
    }

    public void setCsGroupCount(String csGroupCount) {
        this.csGroupCount = csGroupCount;
    }

    public String getAppointment_made_by() {
        return appointment_made_by;
    }

    public void setAppointment_made_by(String appointment_made_by) {
        this.appointment_made_by = appointment_made_by;
    }

    public String getAppointment_date_h() {
        return appointment_date_h;
    }

    public void setAppointment_date_h(String appointment_date_h) {
        this.appointment_date_h = appointment_date_h;
    }

    public String getCf_appointment_type() {
        return cf_appointment_type;
    }

    public void setCf_appointment_type(String cf_appointment_type) {
        this.cf_appointment_type = cf_appointment_type;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getRequestingDocName() {
        return requestingDocName;
    }

    public void setRequestingDocName(String requestingDocName) {
        this.requestingDocName = requestingDocName;
    }

    public String getRmc_ord() {
        return rmc_ord;
    }

    public void setRmc_ord(String rmc_ord) {
        this.rmc_ord = rmc_ord;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
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

    @Override
    public int compareTo(Request request) {


        //Order them in ascending order
        return this.getHourofAppointment() - request.getHourofAppointment();

    }


    public int getHourofAppointment()
    {
        String appHour = getAppointment_time();

        if(appHour != null && appHour.contains(":"))
        {
            appHour = appHour.split(":")[0];

            return Integer.parseInt(appHour);


        }else return Integer.MAX_VALUE;
    }

    public boolean isInpatient() {
        return inpatient;
    }

    public void setInpatient(boolean inpatient) {
        this.inpatient = inpatient;
    }
}
