package com.degla.beans;

import com.degla.db.models.ActorEO;
import com.degla.db.models.Employee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

/**
 * Created by snouto on 03/05/2015.
 */
@ManagedBean(name="dashboardBean")
@SessionScoped
/*@Controller("dashboardBean")*/
public class DashboardBean {


    private
    SystemService systemService;

    private ActorEO account;



    @PostConstruct
    public void init()
    {
        try {
            systemService = SpringSystemBridge.services();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getTotalNewFiles()
    {
        try
        {

            return systemService.getRequestsManager().getTotalNewRequests();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }


    public long getTotalMissingFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalMissingFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public void onUpdateAccount(ActionEvent event)
    {
        try
        {
            //updating the current employee
            Employee emp = (Employee)this.getAccount();
            boolean result = systemService.getEmployeeService().updateEntity(emp);

            if(result)
            {
                WebUtils.addMessage("Profile has been updated Successfully");
            }
            else
            {
                WebUtils.addMessage("There is a problem during updating Your Account,Contact System Support");
            }

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }



    public long getTotalRoles()
    {
        try
        {
            return systemService.getRoleService().getMaxResults();

        }catch (Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long getTotalEmployees()
    {
        try
        {
            return systemService.getEmployeeService().getMaxResults();

        }catch (Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }


    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public ActorEO getAccount() {
        return account;
    }

    public void setAccount(ActorEO account) {
        this.account = account;
    }
}
