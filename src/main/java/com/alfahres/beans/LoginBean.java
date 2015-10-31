package com.alfahres.beans;

import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.security.JSFUtils;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Created by snouto on 02/05/2015.
 */
public class LoginBean {

    public static final String SUCCESS="success";
    public static final String FAILURE="failure";
    public static final String LOGOUT = "LOGOUT";

    public static final String ADMIN_PATH="/admin/index.xhtml";
    public static final String COORDINATOR_PATH = "/coordinator/index.xhtml";

    private String username;
    private String password;
    private String routePath = "/login.xhtml";
    protected SystemService systemService;




    //TODO : Important , Remove this later.
    @PostConstruct
    public void onInit()
    {
       try
       {
           systemService = SpringSystemBridge.services();
           /*RoleEO role = new RoleEO(RoleTypes.ADMIN.toString(),RoleTypes.ADMIN.toString(),"Admin Role");
           //systemService.getRoleService().addEntity(role);
           Employee emp = new Employee();
           emp.setEmpID("123456");
           emp.setPassword("snouto");
           emp.setUserName("snouto");
           emp.setRole(role);
           emp.setFirstName("Mohamed");
           emp.setLastName("Ibrahim");*/
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
            //invalidate the session in here
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

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

                setAccessDecision();
                return SUCCESS;

            }else throw new Exception("Login failed");

        }catch(Exception s)
        {
            WebUtils.addMessage("Invalid Credentials , Please Try again !");
            s.printStackTrace();
            return FAILURE;
        }
    }


    private void setAccessDecision()
    {
        DashboardBean dashboardBean = (DashboardBean) JSFUtils.getAnyBeanByName("dashboardBean",DashboardBean.class);

        if(dashboardBean != null && dashboardBean.getAccount() != null)
        {
            RoleTypes type = RoleTypes.valueOf(dashboardBean.getAccount().getRole().getName());

            if(type.isInPatientActor())
                setRoutePath(COORDINATOR_PATH);
            else
                setRoutePath(ADMIN_PATH);


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

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }


}
