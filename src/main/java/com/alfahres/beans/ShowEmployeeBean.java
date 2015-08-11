package com.alfahres.beans;

import com.degla.db.models.Employee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * Created by snouto on 08/05/2015.
 */
public class ShowEmployeeBean {

    public static String SHOWEMPLOYEE = "UPDATEEMPLOYEENOW";
    public static String CANCEL_SHOWEMPLOYEES = "CANCELANDSHOWEMPS";


    private SystemService systemService;



   private GenericLazyDataModel<Employee> employees;


    @PostConstruct
    public void init()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setEmployees(new GenericLazyDataModel<Employee>(systemService.getEmployeeService()));


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onEditAction(ActionEvent actionEvent)
    {

        FacesContext context = FacesContext.getCurrentInstance();
        Object empID = context.getExternalContext().getRequestParameterMap().get("empID");

        if(empID != null)
        {
            Employee emp = systemService.getEmployeeService().getEntity(Integer.parseInt(empID.toString()));


        }



    }






    public GenericLazyDataModel<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(GenericLazyDataModel<Employee> employees) {
        this.employees = employees;
    }



}
