package com.alfahres.beans;

import com.degla.db.models.Employee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Map;

/**
 * Created by snouto on 08/05/2015.
 */
public class ShowEmployeeBean {

    public static String SHOWEMPLOYEE = "UPDATEEMPLOYEENOW";
    public static String CANCEL_SHOWEMPLOYEES = "CANCELANDSHOWEMPS";


    private SystemService systemService;

    private Employee selectedEmployee;



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

    public void toggleActivation(ActionEvent event)
    {
        try {
            //Get the param
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();

            if (parameters != null && parameters.size() > 0) {
                String Id = parameters.get("empID");

                if (systemService == null) systemService = SpringSystemBridge.services();

                Employee currentEmployee = systemService.getEmployeeService().getEntity(Integer.parseInt(Id));

                if (currentEmployee != null) {
                    currentEmployee.setActive(!currentEmployee.isActive());

                    //now update the employee
                    boolean result = systemService.getEmployeeService().updateEntity(currentEmployee);

                    if (result) {
                        WebUtils.addMessage("Employee has been Activated/Deactivated Successfully");
                    } else
                        WebUtils.addMessage("There is a problem , please try again !");
                }
            }
        }catch (Exception s)
        {
            s.printStackTrace();
        }


    }






    public GenericLazyDataModel<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(GenericLazyDataModel<Employee> employees) {
        this.employees = employees;
    }


    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }
}
