package com.alfahres.beans.files;

import com.degla.db.models.*;
import com.degla.restful.utils.EmployeeUtils;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileStateUtils;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 10/07/15.
 */
public class ChangeFileStatusBean implements Serializable{

    private PatientFile file;

    private SystemService systemService;

    //Bindable Values
    private String fileNumber;
    private Employee assignedEmployee;
    private String status;
    private FileStateUtils states;
    private String shelfNumber;
    private String selectedAppointment;

    private ViewHelperBean viewHelper;

    private boolean ok;

    private List<Appointment> appointments;

    private String physicalFile;


    private boolean requiresAppointmentDate;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            this.setAppointments(new ArrayList<Appointment>());
            //get the id from the request parameter
            FacesContext context = FacesContext.getCurrentInstance();
            String idValue = context.getExternalContext().getRequestParameterMap().get("id");
            setStates(new FileStateUtils());

            if(idValue != null) {
                this.file = systemService.getFilesService().getFileWithNumber(idValue);
                this.setFileNumber(this.file.getFileID());
                this.setAssignedEmployee(this.file.getCurrentStatus().getOwner());
                this.setStatus(this.file.getCurrentStatus().getReadableState());
                this.setShelfNumber(this.file.getShelfId());
                this.getViewHelper().setTempFile(this.file);
                this.setSelectedAppointment(String.valueOf(this.file.getCurrentStatus().getAppointment().getId()));

                if (this.getViewHelper().getTempFile() != null) {
                    this.setAppointments(systemService.getAppointmentManager().getMostRecentAppointmentsFor(this.getViewHelper().getTempFile().getFileID()));

                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();

        }
    }






    public void onStatusChange(){

        try
        {
            if(this.getAssignedEmployee() != null && this.getStatus() != null)
            {
                List<FileStates> availableStates = EmployeeUtils.getAllowedStatesFor(this.getAssignedEmployee());

                FileStateUtils utils = new FileStateUtils();

                if(!availableStates.contains(utils.getState(this.getStatus())))
                {
                    WebUtils.addMessage("Assigned Employee can't process the chosen status,Please either change the status or the chosen employee to proceed");
                    this.setOk(false);
                }else
                {
                    this.setOk(true);
                }

                this.checkRequiresAppointmentDate();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    public void checkRequiresAppointmentDate()
    {
        FileStateUtils utils = new FileStateUtils();

        this.setRequiresAppointmentDate(utils.getState(this.getStatus()) == FileStates.OUT_OF_CABIN ||
                utils.getState(this.getStatus()) == FileStates.CHECKED_OUT ||
                utils.getState(this.getStatus()) == FileStates.DISTRIBUTED ||
                utils.getState(this.getStatus()) == FileStates.COORDINATOR_IN);

    }

    public void onUpdate(ActionEvent event)
    {
        try
        {

            FileStates chosenState = getStates().getState(this.getStatus());



            if(this.file == null)
            {
                if(systemService == null)
                    systemService = SpringSystemBridge.services();

                this.file = systemService.getFilesService().getFileWithNumber(this.getFileNumber());
            }

            FileHistory currentStatus = this.file.getCurrentStatus();
            FileHistory newStatus = new FileHistory();


            if(getSelectedAppointment() == null) return;

            Appointment selectedOne = systemService.getAppointmentManager().getEntity(Integer.parseInt(getSelectedAppointment()));
            if(selectedOne == null) return;
            //Set the same appointment for that file
            selectedOne.setActive(false);
            systemService.getAppointmentManager().updateEntity(selectedOne);
            newStatus.setAppointment(selectedOne);
            newStatus.setContainerId(currentStatus.getContainerId());
            newStatus.setCreatedAt(new Date());
            newStatus.setOwner(this.getAssignedEmployee());
            newStatus.setPatientFile(this.file);

            newStatus.setState(chosenState);

            if(this.getShelfNumber() != null && !this.getShelfNumber().isEmpty())
                this.file.setShelfId(this.getShelfNumber());

            //Now add the new file history to the patient file
            this.file.setCurrentStatus(newStatus);

            //now update the current file
            boolean result = systemService.getFilesService().updateEntity(this.file);

            if(result)
            {
               // this.checkForTransfer(file);
                //Notify the user
                WebUtils.addMessage("File has been updated Successfully.");

            }else
            {
                WebUtils.addMessage("There was a problem updating the current file , Please contact your system" +
                        " administrator");

            }


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }




    public List<SelectItem> getItems()
    {
        List<SelectItem> items = new ArrayList<SelectItem>();

        String[] states = getStates().getStates();

        FileStateUtils stateUtils = new FileStateUtils();

        for(String state : states)
        {
            //Exclude the new and checked-in states from the list
            if(stateUtils.getState(state)== FileStates.NEW || stateUtils.getState(state)==FileStates.CHECKED_IN ||
                    stateUtils.getState(state) == FileStates.PROCESSING_COORDINATOR ||
            stateUtils.getState(state) == FileStates.CODING_COORDINATOR ||
                    stateUtils.getState(state) == FileStates.ANALYSIS_COORDINATOR ||
                    stateUtils.getState(state) == FileStates.INCOMPLETE_COORDINATOR ||
                    stateUtils.getState(state) == FileStates.KEEPER_IN ) continue;

            SelectItem item = new SelectItem(state,state);
            items.add(item);
        }

        return items;
    }

    public List<Employee> getEmployees()
    {
        List<Employee> emps = new ArrayList<Employee>();

        if(systemService != null)
        {
            emps = systemService.getEmployeeService().getAllEmployees();
        }

        return emps;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FileStateUtils getStates() {
        return states;
    }

    public void setStates(FileStateUtils states) {
        this.states = states;
    }

    public String getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }



    public ViewHelperBean getViewHelper() {
        return viewHelper;
    }

    public void setViewHelper(ViewHelperBean viewHelper) {
        this.viewHelper = viewHelper;
    }


    public String getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(String selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public boolean isRequiresAppointmentDate() {
        return requiresAppointmentDate;
    }

    public void setRequiresAppointmentDate(boolean requiresAppointmentDate) {
        this.requiresAppointmentDate = requiresAppointmentDate;
    }

    public String getPhysicalFile() {
        return physicalFile;
    }

    public void setPhysicalFile(String physicalFile) {
        this.physicalFile = physicalFile;

        String idValue = physicalFile;

        if(idValue != null) {
            this.file = systemService.getFilesService().getFileWithNumber(idValue);
            this.setFileNumber(this.file.getFileID());
            this.setAssignedEmployee(this.file.getCurrentStatus().getOwner());
            this.setStatus(this.file.getCurrentStatus().getReadableState());
            this.setShelfNumber(this.file.getShelfId());
            this.getViewHelper().setTempFile(this.file);
            this.setSelectedAppointment(String.valueOf(this.file.getCurrentStatus().getAppointment().getId()));

            if (this.getViewHelper().getTempFile() != null) {
                this.setAppointments(systemService.getAppointmentManager().getMostRecentAppointmentsFor(this.getViewHelper().getTempFile().getFileID()));

            }
        }
    }
}
