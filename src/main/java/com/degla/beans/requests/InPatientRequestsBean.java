package com.degla.beans.requests;

import com.degla.beans.DashboardBean;
import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by snouto on 25/07/15.
 */
@ManagedBean(name="inpatientBean")
@RequestScoped
public class InPatientRequestsBean implements Serializable {

    private String fileNumber;
    private String patientName;
    private String patientNumber;
    private String admissionLocation;
    private String admissionDate;
    private String admissionTime;
    private String wardName;
    private String wardNumber;
    private String doctorName;
    private String doctorCode;
    private SystemService systemService;


    @ManagedProperty(value="#{dashboardBean}")
    private DashboardBean dashboardBean;


    @PostConstruct
    public void onInit()
    {
        try
        {
            this.systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onSubmit(ActionEvent event)
    {
            List<Employee> keepers = systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true);

        try
        {
            if(keepers == null || keepers.size() <=0)
            {
                WebUtils.addMessage("Can't Add new Inpatient Requests while there is no Active Keepers in the system");
                return;
            }

            //Create a new Request in here
            Request inpatient = new Request();

            BarcodeUtils utils = new BarcodeUtils(this.getFileNumber());

            String patientFileNumber = this.getFileNumber();

            if(!utils.isNewFileStructure())
                patientFileNumber = utils.getNewBarcodeStructure();



            boolean requestExists = (systemService.getRequestsManager().getSingleRequest(patientFileNumber) != null);

            if(requestExists)
            {
                //it must be added as a transfer request instead
                WebUtils.addMessage(String.format("Request Number : %s already exists , It is already requested by Out-Patient Clinics " +
                        "It can't be requested by Out-Patient Clinics , Just Does not make sense !",patientFileNumber));
                //Clear the request Number
                this.setFileNumber("");

                return;
            }

            inpatient.setFileNumber(patientFileNumber);
            inpatient.setPatientName(this.getPatientName());
            inpatient.setPatientNumber(this.getPatientNumber());
            inpatient.setFileCurrentLocation(this.getAdmissionLocation());
            inpatient.setAppointment_Date(this.getAdmissionDate());
            inpatient.setAppointment_time(this.getAdmissionTime());
            inpatient.setClinicName(this.getWardName());
            inpatient.setClinicCode(this.getWardNumber());
            inpatient.setRequestingDocName(this.getDoctorName());
            inpatient.setClinic_Doc_Code(this.getDoctorCode());
            inpatient.setUserName(getDashboardBean().getAccount().toString());

            //Set a random Batch Request Number
            Random random = new Random();
            int batchRequestNumber = random.nextInt(999999);
            inpatient.setBatchRequestNumber(String.valueOf(batchRequestNumber));

            //this request is inpatient
            inpatient.setInpatient(true);
            //try to assign it to a given keeper employee randomly
            this.randomlyAssignInpatientRequest(inpatient,keepers);
            //now try to insert the current request
            boolean result = systemService.getRequestsManager().addEntity(inpatient);

            if(result)
            {
                WebUtils.addMessage("Inpatient Request was successfully added and assigned to a keeper");
            }else
            {
                WebUtils.addMessage("There was a problem adding the inpatient request , please contact your system administrator");

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
        finally
        {
            this.clear();
        }
    }

    private void clear() {

        this.setAdmissionDate("");
        this.setAdmissionTime("");
        this.setAdmissionLocation("");
        this.setDoctorCode("");
        this.setDoctorName("");
        this.setFileNumber("");
        this.setPatientName("");
        this.setPatientNumber("");
        this.setWardName("");
        this.setWardNumber("");
    }

    private void randomlyAssignInpatientRequest(Request inpatient,List<Employee> keepers) {

        try
        {
            int keepers_size = keepers.size();
            //randomly pick one in this list
            Random rand = new Random();

            int rand_keeper_id = rand.nextInt(keepers_size+1); // to include the last Employee in the randomization process

            Employee chosenEmployee = keepers.get(rand_keeper_id);

            inpatient.setAssignedTo(chosenEmployee);

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
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

    public String getAdmissionLocation() {
        return admissionLocation;
    }

    public void setAdmissionLocation(String admissionLocation) {
        this.admissionLocation = admissionLocation;
    }


    public String getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(String admissionTime) {
        this.admissionTime = admissionTime;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }
}
