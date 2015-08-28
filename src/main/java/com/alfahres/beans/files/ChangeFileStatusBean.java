package com.alfahres.beans.files;

import com.degla.db.models.*;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileRouter;
import com.degla.utils.FileStateUtils;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            //get the id from the request parameter
            FacesContext context = FacesContext.getCurrentInstance();
            String idValue = context.getExternalContext().getRequestParameterMap().get("id");
            setStates(new FileStateUtils());

            if(idValue != null)
            {
                this.file = systemService.getFilesService().getFileWithNumber(idValue);

                this.setFileNumber(this.file.getFileID());
                this.setAssignedEmployee(this.file.getCurrentStatus().getOwner());
                this.setStatus(this.file.getCurrentStatus().getReadableState());
                this.setShelfNumber(this.file.getShelfId());
            }

        }catch (Exception s)
        {
            s.printStackTrace();

        }
    }

    public void onUpdate(ActionEvent event)
    {
        try
        {

            FileStates chosenState = getStates().getState(this.getStatus());

            if(chosenState == FileStates.CHECKED_IN && (this.getShelfNumber() == null || this.getShelfNumber().isEmpty()))
            {
                WebUtils.addMessage("You have to select a shelf number for archiving that file ");
                return;
            }

            if(this.file == null)
            {
                if(systemService == null)
                    systemService = SpringSystemBridge.services();

                this.file = systemService.getFilesService().getFileWithNumber(this.getFileNumber());
            }

            FileHistory currentStatus = this.file.getCurrentStatus();
            FileHistory newStatus = new FileHistory();
            newStatus.setAppointment_Hijri_Date(currentStatus.getAppointment_Hijri_Date());
            newStatus.setAppointment_Made_by(currentStatus.getAppointment_Made_by());
            newStatus.setAppointmentType(currentStatus.getAppointmentType());
            newStatus.setBatchRequestNumber(currentStatus.getBatchRequestNumber());
            newStatus.setClinicCode(currentStatus.getClinicCode());
            newStatus.setClinicDocCode(currentStatus.getClinicDocCode());
            newStatus.setClinicDocName(currentStatus.getClinicDocName());
            newStatus.setClinicName(currentStatus.getClinicName());
            newStatus.setContainerId(currentStatus.getContainerId());
            newStatus.setCreatedAt(new Date());
            newStatus.setAppointment_Date_G(currentStatus.getAppointment_Date_G());
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
                this.checkForTransfer(file);
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

    private void checkForTransfer(PatientFile file) {

        try
        {
           List<Transfer> transferList = systemService.getTransferManager().getFutureTransfer(file.getFileID());

            if(transferList != null && transferList.size() > 0)
            {
                try
                {
                    if(file.getCurrentStatus().getState() == FileStates.CHECKED_IN)
                    {
                        Transfer futureTransfer = transferList.get(0);

                        //That means it is properly a new request, so add it
                        Request transferRequest = futureTransfer.toRequestObject();

                        //Route the current request
                        List<Request> tempRequests = new ArrayList<Request>();
                        tempRequests.add(transferRequest);

                        FileRouter router = new FileRouter(systemService.getEmployeeService());
                        router.routeFiles(tempRequests);

                        //after that , try to add the current request into the database
                        boolean result = systemService.getRequestsManager().addEntity(transferRequest);
                        result &= systemService.getTransferManager().removeEntity(futureTransfer);
                    }

                }catch (Exception s)
                {
                    s.printStackTrace();
                    return;
                }
            }

        }catch (Exception s)
        {
            System.out.println(s.getMessage());
        }
    }


    public List<SelectItem> getItems()
    {
        List<SelectItem> items = new ArrayList<SelectItem>();

        String[] states = getStates().getStates();

        for(String state : states)
        {
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
}
