package com.degla.beans;

import com.degla.db.models.Employee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.EmployeeLazyModel;
import com.degla.utils.GenericLazyDataModel;
import org.primefaces.model.LazyDataModel;
import sun.net.www.content.text.Generic;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by snouto on 08/05/2015.
 */
@ManagedBean(name="showEmployeeBean")
@ViewScoped
public class ShowEmployeeBean {

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




    public GenericLazyDataModel<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(GenericLazyDataModel<Employee> employees) {
        this.employees = employees;
    }
}
