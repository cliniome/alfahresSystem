package com.degla.controllers;

import com.degla.db.models.Employee;
import com.degla.restful.models.RestfulEmployee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

/**
 * Created by snouto on 20/05/15.
 */
public class EmployeeController {

    private SystemService systemService;


    public EmployeeController(){

        try
        {
            systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    /**
     * This method will update the current employee profile
     * @param emp - the Restful Employee Representation that will be used to update the current employee
     * @return true in case of success or false in case of failure
     */
    public boolean updateProfile(RestfulEmployee emp)
    {
        try
        {
            //select the employee from the database based on his username
            Employee currentEmp = systemService.getEmployeeService().getEmployeeByUserName(emp.getUserName());

            if(currentEmp == null) throw new Exception("Employee was not found");

            currentEmp.setFirstName(emp.getFirstName());
            currentEmp.setLastName(emp.getLastName());


            //now update the current employee
            return systemService.getEmployeeService().updateEntity(currentEmp);

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }
}
