package com.degla.beans;

import com.degla.dao.EmployeeDAO;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */
@ManagedBean
@RequestScoped
@Controller
public class LoginBean {

    @Autowired
    EmployeeDAO employeeDAO;


    public String Login()
    {
        Employee newemp = new Employee();
        newemp.setEmpID("123456");
        newemp.setFirstName("mohamed");
        newemp.setLastName("ibrahim");
        newemp.setPassword("snouto");
        newemp.setUserName("snouto");
        newemp.setRole(new RoleEO(RoleTypes.ADMIN.toString(),"Admin","nothing in here"));

       boolean result = employeeDAO.addEntity(newemp);


        System.out.println(result);

        return null;
    }
}
