package com.alfahres.beans.requests;

import com.degla.db.models.Appointment;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 21/07/15.
 */
public class RedistributeRequestsBean implements Serializable {


    private SystemService systemService;
    private Employee fromKeeper;
    private DualListModel<Employee> toKeepersList;
    private DualListModel<Employee> shufflingEmployees;
    private List<Employee> picker_toEmployees;
    private List<Employee> picker_fromEmployees;
    private List<Employee> shuffler_toEmployees;
    private List<Employee> shuffler_fromEmployees;
    private Date appointmentDate;

    private boolean distributionEnabled = true;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            this.setPicker_fromEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString()));
            this.setShuffler_fromEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(), true));
            this.setPicker_toEmployees(new ArrayList<Employee>());
            this.setShuffler_toEmployees(new ArrayList<Employee>());
            this.setToKeepersList(new DualListModel<Employee>(getPicker_fromEmployees(), getPicker_toEmployees()));
            this.setShufflingEmployees(new DualListModel<Employee>(getShuffler_fromEmployees(),getShuffler_toEmployees()));

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void ajaxListener()
    {
        try
        {
            if(this.getFromKeeper() != null)
            {
                if(!getFromKeeper().isActive())
                    setDistributionEnabled(false);
                else setDistributionEnabled(true);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @PreDestroy
    public void onClear()
    {
        this.setPicker_toEmployees(new ArrayList<Employee>());
        this.setToKeepersList(new DualListModel<Employee>(getPicker_fromEmployees(), getPicker_toEmployees()));
    }


    public void shuffleAppointments(ActionEvent event){

        try
        {
            if(this.getShufflingEmployees().getTarget() == null || this.getShufflingEmployees().getTarget().size() <= 0)
            {
                WebUtils.addMessage("Please choose Keepers to shuffle Appointments among Them");
                return;
            }

            if(this.getShufflingEmployees().getTarget().size() <= 1)
            {
                WebUtils.addMessage("Please Choose two or more Employees to shuffle appointments among them");
                return;
            }

            if(systemService == null)
                systemService = SpringSystemBridge.services();

            //get the selected keepers
            List<Employee> selectedKeepers = this.getShufflingEmployees().getTarget();
            List<String> userNames= new ArrayList<String>();

            for(Employee emp : selectedKeepers)
            {
                userNames.add(emp.getUsername());
            }

            //getAppointments of those Employees
            List<Appointment> selectedAppointments = systemService.getAppointmentManager().getAppointmentsForUsers(userNames,this.getAppointmentDate());

            if(selectedAppointments == null || selectedAppointments.size() <= 0 )
            {
                WebUtils.addMessage("There are no available Appointments to Shuffle for selected Employees");
                return;
            }

            //now route those appointments
            systemService.getFileRouter().routeFiles(selectedAppointments,selectedKeepers);

            //now save them back again
            boolean result = true;

            for(Appointment assignedAppointment : selectedAppointments)
            {
                //now update that

                result &= systemService.getAppointmentManager().updateEntity(assignedAppointment);
            }


            if(result)
            {
                WebUtils.addMessage("Selected Employees' Appointments have been successfully shuffled");

            }


        }catch (Exception s)
        {
            s.printStackTrace();
            WebUtils.addMessage("There was a problem shuffling appointments among selected keepers , please contact your system administrator.");

        }

    }

    public void redistributeRequests(ActionEvent event)
    {
        try
        {
            if(this.fromKeeper == null)
            {
                WebUtils.addMessage("Please Choose a Keeper to select his/her Requests First before proceeding.");
                return;
            }

            if(this.getToKeepersList().getTarget() == null || this.getToKeepersList().getTarget().size() <=0)
            {
                WebUtils.addMessage("Please choose Keeper(s) to redistribute requests to them first before " +
                        "proceeding");
                return;
            }

            List<Appointment> availableRequests = systemService.getAppointmentManager().getNewRequestsFor(this.fromKeeper.getUsername());

            if(availableRequests == null || availableRequests.size() <=0)
            {
                WebUtils.addMessage("There are no Assigned Requests for the selected keeper " +
                        ", Please Try another Employee.");
                return;
            }

            List<Employee> toKeepers = this.getToKeepersList().getTarget();

            //Now route those requests
            systemService.getFileRouter().routeFiles(availableRequests,(List<Employee>)toKeepers);

            //then update those requests
            boolean updateResult = true;

            for(Appointment currentRequest : availableRequests)
            {
                //update them respectively
               updateResult &= systemService.getAppointmentManager().updateEntity(currentRequest);
            }

            if(updateResult)
            {
                WebUtils.addMessage(String.format("Re-Distributing Requests for Employee %s was Successful",this.fromKeeper.getfullName()));

            }

            //Clear everything
            this.onClear();


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onChange()
    {
        try
        {
            if(this.fromKeeper != null)
            {
                //set the from Employees to all active keepers except fromKeeper
                List<Employee> activeKeepers = systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true);

                List<Employee> filteredEmps = new ArrayList<Employee>();

                if(activeKeepers != null && activeKeepers.size() > 0)
                {
                    for(Employee emp : activeKeepers)
                    {
                        if(emp.getId() != this.fromKeeper.getId())
                            filteredEmps.add(emp);
                    }
                }

                this.setPicker_fromEmployees(filteredEmps);
                this.toKeepersList = new DualListModel<Employee>(this.getPicker_fromEmployees(), this.getPicker_toEmployees());
            }else
            {
                this.setPicker_fromEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true));
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public List<Employee> getKeepers()
    {
        try
        {
            return systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString());

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    public Employee getFromKeeper() {
        return fromKeeper;
    }

    public void setFromKeeper(Employee fromKeeper) {
        this.fromKeeper = fromKeeper;
    }

    public DualListModel<Employee> getToKeepersList() {
        return toKeepersList;
    }

    public void setToKeepersList(DualListModel<Employee> toKeepersList) {
        this.toKeepersList = toKeepersList;
    }

    public List<Employee> getPicker_toEmployees() {
        return picker_toEmployees;
    }

    public void setPicker_toEmployees(List<Employee> picker_toEmployees) {
        this.picker_toEmployees = picker_toEmployees;
    }

    public List<Employee> getPicker_fromEmployees() {
        return picker_fromEmployees;
    }

    public void setPicker_fromEmployees(List<Employee> picker_fromEmployees) {
        this.picker_fromEmployees = picker_fromEmployees;
    }

    public boolean isDistributionEnabled() {
        return distributionEnabled;
    }

    public void setDistributionEnabled(boolean distributionEnabled) {
        this.distributionEnabled = distributionEnabled;
    }

    public DualListModel<Employee> getShufflingEmployees() {
        return shufflingEmployees;
    }

    public void setShufflingEmployees(DualListModel<Employee> shufflingEmployees) {
        this.shufflingEmployees = shufflingEmployees;
    }

    public List<Employee> getShuffler_toEmployees() {
        return shuffler_toEmployees;
    }

    public void setShuffler_toEmployees(List<Employee> shuffler_toEmployees) {
        this.shuffler_toEmployees = shuffler_toEmployees;
    }

    public List<Employee> getShuffler_fromEmployees() {
        return shuffler_fromEmployees;
    }

    public void setShuffler_fromEmployees(List<Employee> shuffler_fromEmployees) {
        this.shuffler_fromEmployees = shuffler_fromEmployees;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
