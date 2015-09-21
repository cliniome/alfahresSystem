package com.alfahres.beans;

import com.degla.db.models.Appointment;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by snouto on 20/05/15.
 */
public class SearchAppointmentsBean {

    private String searchQuery;

    private SystemService systemService;

    private List<Appointment> searchedAppointments;

    private List<Employee> employees;
    private Employee chosenEmployee;

    private String updateResultsLbl;

    @PostConstruct
    public void init()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            employees = systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true);

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }
    /**
     * This method will select the chosen employee from the list
     */
    public void onSelectEmployee()
    {
        try
        {
            if(getChosenEmployee() != null)
            {
                //assign that employee to the selected requests
                if(getSearchedAppointments() != null && getSearchedAppointments().size() > 0)
                {
                    boolean result = true;

                    for(Appointment currentRequest : getSearchedAppointments())
                    {
                        if(currentRequest.isSelected())
                        {
                            currentRequest.setAssignedTo(getChosenEmployee());

                            //now update the current request
                            result &= systemService.getAppointmentManager().updateEntity(currentRequest);
                        }

                    }


                    if(result)
                    {
                        String msg = String.format("Requests have been assigned Successfully to : %s",
                                getChosenEmployee().getfullName());

                        this.setUpdateResultsLbl(msg);


                    }else
                    {
                        String msg = String.format("There was a problem assigning the chosen Requests");
                        this.setUpdateResultsLbl(msg);
                    }
                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public boolean foundResults()
    {
        if(getSearchedAppointments() == null || getSearchedAppointments().size() <=0) return false;
        else return true;
    }

    public int totalCount()
    {
        if(foundResults()) return getSearchedAppointments().size();
        else return 0;
    }

    public void doSearch()
    {
        if(getSearchQuery() != null && getSearchQuery().length() > 0)
        {
            setSearchedAppointments(systemService.getAppointmentManager()
                    .searchAppointments(getSearchQuery()));

            if(getSearchedAppointments() == null || getSearchedAppointments().size() <=0)
                WebUtils.addMessage("There are no available requests");

        }else
            WebUtils.addMessage("You have to supply a search Query");
    }


    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Employee getChosenEmployee() {
        return chosenEmployee;
    }

    public void setChosenEmployee(Employee chosenEmployee) {
        this.chosenEmployee = chosenEmployee;
    }

    public String getUpdateResultsLbl() {
        return updateResultsLbl;
    }

    public void setUpdateResultsLbl(String updateResultsLbl) {
        this.updateResultsLbl = updateResultsLbl;
    }

    public List<Appointment> getSearchedAppointments() {
        return searchedAppointments;
    }

    public void setSearchedAppointments(List<Appointment> searchedAppointments) {
        this.searchedAppointments = searchedAppointments;
    }
}
