package com.degla.beans;

import com.degla.dao.EmployeeDAO;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.security.LoginService;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */
@ManagedBean(name="loginBean")
@ApplicationScoped
/*@Controller*/
public class LoginBean {

    public static final String SUCCESS="success";
    public static final String FAILURE="failure";
    public static final String LOGOUT = "LOGOUT";

    private String username;
    private String password;

    @Autowired
    protected SystemService systemService;


    //TODO : Important , Remove this later.
    @PostConstruct
    public void onInit()
    {
       try
       {
           systemService = SpringSystemBridge.services();
           RoleEO role = new RoleEO(RoleTypes.ADMIN.toString(),RoleTypes.ADMIN.toString(),"Admin Role");
           //systemService.getRoleService().addEntity(role);
           Employee emp = new Employee();
           emp.setEmpID("123456");
           emp.setPassword("snouto");
           emp.setUserName("snouto");
           emp.setRole(role);
           emp.setFirstName("Mohamed");
           emp.setLastName("Ibrahim");
           //systemService.getEmployeeService().addEntity(emp);

       }catch (Exception s)
       {
           s.printStackTrace();
       }


    }

    public String logout()
    {
        try
        {
            systemService.getLoginService().dologOut();

        }catch(Exception s)
        {
            s.printStackTrace();

        }

        return LOGOUT;
    }


    public String login()
    {
        try
        {

            if(systemService.getLoginService().dologin(this.getUsername(), this.getPassword()))
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
