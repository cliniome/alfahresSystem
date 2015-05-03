package com.degla.beans;

import com.degla.db.models.ActorEO;
import com.degla.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by snouto on 03/05/2015.
 */
@ManagedBean
@SessionScoped
@Controller("dashboardBean")
public class DashboardBean {

    @Autowired
    private
    SystemService systemService;

    private ActorEO account;


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
