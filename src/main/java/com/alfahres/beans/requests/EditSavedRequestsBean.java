package com.alfahres.beans.requests;

import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * Created by snouto on 18/06/15.
 */

public class EditSavedRequestsBean implements Serializable {



    private SystemService systemService;
    private String fileNumber;
    private String patientNumber;
    private String patientName;
    private Employee chosenEmployee;
    private List<Employee> employees;
    private Request chosenRequest;

    private boolean route;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true));

            this.loadQueryString();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void deleteRequest(ActionEvent event)
    {
        try
        {

            if(systemService == null)
                systemService = SpringSystemBridge.services();

            if(this.chosenRequest == null)
            {

                this.chosenRequest = systemService.getRequestsManager().getSingleRequest(this.getFileNumber());
            }

            boolean done = systemService.getRequestsManager().removeEntity(this.chosenRequest);

            if(done)
            {
                WebUtils.addMessage("The current Request has been deleted Successfully");
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onSubmit(ActionEvent event)
    {
        try
        {
            if(this.chosenRequest == null)
            {
                //WebUtils.addMessage("There was a problem , Please contact your system administrator");
                this.chosenRequest = this.systemService.getRequestsManager().getSingleRequest(this.getFileNumber());
            }


            this.chosenRequest.setFileNumber(this.getFileNumber());
            this.chosenRequest.setPatientName(this.getPatientName());
            this.chosenRequest.setPatientNumber(this.getPatientNumber());
            this.chosenRequest.setAssignedTo(this.getChosenEmployee());

            boolean result = systemService.getRequestsManager().updateEntity(this.chosenRequest);

            if(result)
            {
                WebUtils.addMessage("Request was updated Successfully.");
            }else
            {
                WebUtils.addMessage("There was a problem updating the current request , please contact your system administrator");
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public String getRouteAction()
    {
        String defaultRoute = "SHOWREQUESTS";

        if(this.isRoute())
            return defaultRoute;
        else
        {
            return "SHOWWATCHLIST";
        }
    }

    private void loadQueryString() throws Exception {

        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("id");

        String routeString = context.getExternalContext().getRequestParameterMap().get("route");

        if(id == null || routeString == null) return;

        int routeInt = Integer.parseInt(routeString);

        this.setRoute((routeInt > 0) ? true : false);

        this.chosenRequest = systemService.getRequestsManager().getSingleRequest(id);

        if(this.chosenRequest != null)
        {
            this.chosenEmployee = this.chosenRequest.getAssignedTo();
            this.setFileNumber(this.chosenRequest.getFileNumber());
            this.setPatientName(this.chosenRequest.getPatientName());
            this.setPatientNumber(this.chosenRequest.getPatientNumber());
        }

    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Employee getChosenEmployee() {
        return chosenEmployee;
    }

    public void setChosenEmployee(Employee chosenEmployee) {
        this.chosenEmployee = chosenEmployee;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public boolean isRoute() {
        return route;
    }

    public void setRoute(boolean route) {
        this.route = route;
    }
}
