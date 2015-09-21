package com.alfahres.beans.requests;

import com.alfahres.beans.DashboardBean;
import com.degla.db.models.*;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by snouto on 25/07/15.
 */
public class InPatientRequestsBean implements Serializable {

    private String fileNumber;
    private String patientName;
    private String patientNumber;
    private String admissionLocation;
    private Date admissionDate;
    private Date admissionTime;
    private String wardName;
    private String wardNumber;
    private String doctorName;
    private String doctorCode;
    private SystemService systemService;


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
                WebUtils.addMessage("Can't Add new Inpatient Requests while there are no Active Keepers in the system");
                return;
            }

            //Create a new Request in here
            Appointment inpatient = new Appointment();

            BarcodeUtils utils = new BarcodeUtils(this.getFileNumber());

            String patientFileNumber = this.getFileNumber();

            if(!utils.isNewFileStructure())
                patientFileNumber = utils.getNewBarcodeStructure();


            /*if(requestExists)
            {
                //it must be added as a transfer request instead
                WebUtils.addMessage(String.format("Request Number : %s already exists , It is already requested! ",patientFileNumber));
                //Clear the request Number
                this.setAppointmentId("");

                return;
            }*/

            inpatient.setFileNumber(patientFileNumber);
            inpatient.setPatientName(this.getPatientName());
            inpatient.setPatientNumber(this.getPatientNumber());
            inpatient.setFileCurrentLocation(this.getAdmissionLocation());
            //Parse the date
            /*Date appointmentDate = DateUtils.parseDate(this.getAdmissionDate(),systemService.getDatePatternsBean().getDatePatterns().toArray(new String[]{}));
            Calendar calc = Calendar.getInstance();
            calc.setTime(appointmentDate);
            calc.add(Calendar.HOUR,this.getHour());
            calc.add(Calendar.MINUTE,this.getMinute());
            inpatient.setAppointment_Date(calc.getTime());*/
            /*inpatient.setAppointment_time(this.getAdmissionTime());*/

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String time = formatter.format(this.getAdmissionTime());
            inpatient.setAppointment_time(time);

            Calendar calc = Calendar.getInstance();
            calc.setTime(this.getAdmissionTime());
            int hour = calc.get(Calendar.HOUR);
            int minute = calc.get(Calendar.MINUTE);
            calc.setTime(this.getAdmissionDate());
            calc.add(Calendar.HOUR,hour);
            calc.add(Calendar.MINUTE,minute);
            inpatient.setAppointment_Date(calc.getTime());
            inpatient.setClinicName(this.getWardName());
            inpatient.setClinicCode(this.getWardNumber());
            inpatient.setRequestingDocName(this.getDoctorName());
            inpatient.setClinic_Doc_Code(this.getDoctorCode());
            inpatient.setUserName(getDashboardBean().getAccount().toString());

            //Set a random Batch Request Number
            Random random = new Random();
            int batchRequestNumber = random.nextInt(999999);
            inpatient.setBatchRequestNumber(String.valueOf(batchRequestNumber));

            inpatient.setInpatient(true);



            //this request is inpatient

            //try to assign it to a given keeper employee randomly
            this.randomlyAssignInpatientRequest(inpatient,keepers);
            //now try to insert the current request
            boolean result = systemService.getAppointmentManager().addEntity(inpatient);

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


        this.setAdmissionLocation("");
        this.setDoctorCode("");
        this.setDoctorName("");
        this.setFileNumber("");
        this.setPatientName("");
        this.setPatientNumber("");
        this.setWardName("");
        this.setWardNumber("");
    }

    private void randomlyAssignInpatientRequest(Appointment inpatient,List<Employee> keepers) {

        try
        {
            int keepers_size = keepers.size();
            //randomly pick one in this list
            Random rand = new Random();

            int rand_keeper_id = rand.nextInt(keepers_size); // to include the last Employee in the randomization process

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


    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(Date admissionTime) {
        this.admissionTime = admissionTime;
    }
}
