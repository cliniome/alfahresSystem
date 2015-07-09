package com.degla.beans;

import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by snouto on 20/05/15.
 */
@ManagedBean(name="searchRequestsBean")
@ViewScoped
public class SearchRequestsBean {

    private String searchQuery;

    private SystemService systemService;

    private List<Request> searchedRequests;

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
                if(getSearchedRequests() != null && getSearchedRequests().size() > 0)
                {
                    boolean result = true;

                    for(Request currentRequest : getSearchedRequests())
                    {
                        if(currentRequest.isSelected())
                        {
                            currentRequest.setAssignedTo(getChosenEmployee());

                            //now update the current request
                            result &= systemService.getRequestsManager().updateEntity(currentRequest);
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
        if(searchedRequests == null || searchedRequests.size() <=0) return false;
        else return true;
    }

    public int totalCount()
    {
        if(foundResults()) return searchedRequests.size();
        else return 0;
    }

    public void doSearch()
    {
        if(getSearchQuery() != null && getSearchQuery().length() > 0)
        {
            searchedRequests = systemService.getRequestsManager().searchRequests(getSearchQuery());

            if(searchedRequests == null || searchedRequests.size() <=0)
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

    public List<Request> getSearchedRequests() {
        return searchedRequests;
    }

    public void setSearchedRequests(List<Request> searchedRequests) {
        this.searchedRequests = searchedRequests;
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
}
