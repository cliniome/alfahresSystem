package com.alfahres.beans.dashboard;

import com.degla.db.models.Clinic;
import com.degla.db.models.FileStates;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will represent the overall dashboard statistics metrics/badges on the main Dashboard UI Screen
 * Created by snouto on 09/10/15.
 */
public class DashboardMetrics implements Serializable {



    private SystemService systemService;
    private Date appointmentDate;
    private Date operationDate;
    private Clinic chosenClinic;
    private String clinicName;
    private int option;
    public static final int APPOINTMENT_DATE = 0;
    public static final int OPERATION_DATE = 1;
    public static final int CLINIC = 2;




    private long archivedFiles;
    private long newRequests;
    private long watchListRequests;
    private long inSortingFiles;
    private long checkedoutFiles;
    private long atCoordinator;
    private long atClinics;
    private long missingFiles;
    private long receivedByReceptionist;


    public DashboardMetrics(){

        this.initMetrics();
    }


    public void openSelectClinicDialog()
    {
        try
        {
            Map<String,Object> props = new HashMap<String, Object>();

            props.put("modal",true);
            props.put("closable",true);

            RequestContext.getCurrentInstance().openDialog("selectClinic",props,null);


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onClinicChosen(SelectEvent event)
    {
        try
        {
            Clinic clinic =(Clinic)event.getObject();

            if(clinic != null)
            {
                this.setChosenClinic(clinic);
                this.setClinicName(clinic.getClinicName());
                this.getCalculatedMetricsByClinic(clinic.getClinicCode());

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    /**
     * This function will calculate all metrics in terms of the chosen clinic
     */



    public void initMetrics()
    {
        try
        {
            this.systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onRadioChange()
    {
        switch (getOption())
        {
            case APPOINTMENT_DATE:
                setOperationDate(null);
                setClinicName(null);
                break;
            case OPERATION_DATE:
                setAppointmentDate(null);
                setClinicName(null);
                break;
            case CLINIC:
                setAppointmentDate(null);
                setOperationDate(null);
                break;
        }
    }

    private void getCalculatedMetricsByClinic(String clinicCode)
    {
        this.setArchivedFiles(systemService.getFilesService().getTotalCheckedInFiles_ByClinic(clinicCode));
        this.setNewRequests(systemService.getAppointmentManager().getTotalNewAppointments_ByClinicCode(clinicCode));
        this.setWatchListRequests(systemService.getAppointmentManager().getCountOfWatchListRequests_ByClinicCode(clinicCode));
        this.setInSortingFiles(systemService.getFilesService().getFilesCountForState_ByClinic(FileStates.OUT_OF_CABIN, clinicCode));
        this.setCheckedoutFiles(systemService.getFilesService().getFilesCountForState_ByClinic(FileStates.CHECKED_OUT, clinicCode));
        this.setAtCoordinator(systemService.getFilesService().getFilesCountForState_ByClinic(FileStates.COORDINATOR_IN, clinicCode));
        this.setAtClinics(systemService.getFilesService().getFilesCountForState_ByClinic(FileStates.DISTRIBUTED, clinicCode));
        this.setMissingFiles(systemService.getFilesService().getFilesCountForState_ByClinic(FileStates.MISSING,clinicCode));

    }


    public void onDateSelect(SelectEvent event)
    {
       try
       {
           if(this.systemService == null)
               this.systemService = SpringSystemBridge.services();


           switch (getOption())
           {
               case APPOINTMENT_DATE:
               {
                   this.setArchivedFiles(systemService.getFilesService().getTotalCheckedInFiles_AppointmentDate(getAppointmentDate()));
                   this.setNewRequests(systemService.getAppointmentManager().getTotalNewAppointments_AppointmentDate(getAppointmentDate()));
                   this.setWatchListRequests(systemService.getAppointmentManager().getCountOfWatchListRequests_AppointmentDate(getAppointmentDate()));
                   this.setInSortingFiles(systemService.getFilesService().getFilesCountForState_AppointmentDate(FileStates.OUT_OF_CABIN, getAppointmentDate()));
                   this.setCheckedoutFiles(systemService.getFilesService().getTotalCheckedOutFiles_AppointmentDate(getAppointmentDate()));
                   this.setAtCoordinator(systemService.getFilesService().getFilesCountForState_AppointmentDate(FileStates.COORDINATOR_IN, getAppointmentDate()));
                   this.setAtClinics(systemService.getFilesService().getFilesCountForState_AppointmentDate(FileStates.DISTRIBUTED, getAppointmentDate()));
                   this.setMissingFiles(systemService.getFilesService().getFilesCountForState_AppointmentDate(FileStates.MISSING, getAppointmentDate()));
                   this.setReceivedByReceptionist(systemService.getFilesService().getFilesCountForState_AppointmentDate(FileStates.RECEPTIONIST_IN, getAppointmentDate()));
               }
               break;
               case OPERATION_DATE:
               {
                   this.setArchivedFiles(systemService.getFilesService().getTotalCheckedInFiles_OperationDate(getOperationDate()));
                   this.setNewRequests(systemService.getAppointmentManager().getTotalNewAppointments());
                   this.setWatchListRequests(systemService.getAppointmentManager().getCountOfWatchListRequests());
                   this.setInSortingFiles(systemService.getFilesService().getFilesCountForState_OperationDate(FileStates.OUT_OF_CABIN, getOperationDate()));
                   this.setCheckedoutFiles(systemService.getFilesService().getTotalCheckedOutFiles_OperationDate(getOperationDate()));
                   this.setAtCoordinator(systemService.getFilesService().getFilesCountForState_OperationDate(FileStates.COORDINATOR_IN, getOperationDate()));
                   this.setAtClinics(systemService.getFilesService().getFilesCountForState_OperationDate(FileStates.DISTRIBUTED, getOperationDate()));
                   this.setMissingFiles(systemService.getFilesService().getFilesCountForState_OperationDate(FileStates.MISSING, getOperationDate()));
                   this.setReceivedByReceptionist(systemService.getFilesService().getFilesCountForState_OperationDate(FileStates.RECEPTIONIST_IN, getOperationDate()));

               }
               break;

               default:
                   break;
           }

       }catch (Exception s)
       {
           s.printStackTrace();
       }
    }





    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Clinic getChosenClinic() {
        return chosenClinic;
    }

    public void setChosenClinic(Clinic chosenClinic) {
        this.chosenClinic = chosenClinic;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }


    public long getArchivedFiles() {
        return archivedFiles;
    }

    public void setArchivedFiles(long archivedFiles) {
        this.archivedFiles = archivedFiles;
    }

    public long getNewRequests() {
        return newRequests;
    }

    public void setNewRequests(long newRequests) {
        this.newRequests = newRequests;
    }

    public long getWatchListRequests() {
        return watchListRequests;
    }

    public void setWatchListRequests(long watchListRequests) {
        this.watchListRequests = watchListRequests;
    }

    public long getInSortingFiles() {
        return inSortingFiles;
    }

    public void setInSortingFiles(long inSortingFiles) {
        this.inSortingFiles = inSortingFiles;
    }

    public long getCheckedoutFiles() {
        return checkedoutFiles;
    }

    public void setCheckedoutFiles(long checkedoutFiles) {
        this.checkedoutFiles = checkedoutFiles;
    }

    public long getAtCoordinator() {
        return atCoordinator;
    }

    public void setAtCoordinator(long atCoordinator) {
        this.atCoordinator = atCoordinator;
    }

    public long getAtClinics() {
        return atClinics;
    }

    public void setAtClinics(long atClinics) {
        this.atClinics = atClinics;
    }

    public long getMissingFiles() {
        return missingFiles;
    }

    public void setMissingFiles(long missingFiles) {
        this.missingFiles = missingFiles;
    }

    public long getReceivedByReceptionist() {
        return receivedByReceptionist;
    }

    public void setReceivedByReceptionist(long receivedByReceptionist) {
        this.receivedByReceptionist = receivedByReceptionist;
    }
}
