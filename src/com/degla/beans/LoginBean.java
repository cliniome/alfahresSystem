package com.degla.beans;

import com.degla.dao.EmployeeDAO;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.security.LoginService;
import com.degla.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */
@ManagedBean
@ViewScoped
@Controller
public class LoginBean {

    public static final String SUCCESS="success";
    public static final String FAILURE="failure";

    private String username;
    private String password;

    @Autowired
    protected LoginService loginService;

    @Autowired
    protected SystemService systemService;


    @PostConstruct
    public void onInit()
    {
        Employee emp = new Employee();
        emp.setEmpID("123456");
        emp.setPassword("snouto");
        emp.setUserName("snouto");
        emp.setRole(new RoleEO(RoleEO.ROLE_ADMIN,"Admin","Admin Role Description...."));
        emp.setFirstName("Mohamed");
        emp.setLastName("Ibrahim");

        systemService.getEmployeeService().addEntity(emp);


    }


    public String login()
    {
        try
        {

            if(loginService.dologin(this.getUsername(),this.getPassword()))
            {
                return SUCCESS;

            }else throw new Exception("Login failed");

        }catch(Exception s)
        {
            s.printStackTrace();
            return FAILURE;
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
