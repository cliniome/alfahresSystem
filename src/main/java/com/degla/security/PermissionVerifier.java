package com.degla.security;

import com.alfahres.beans.DashboardBean;
import com.degla.db.models.ActorEO;
import com.degla.db.models.Employee;

import java.io.Serializable;

/**
 * Created by snouto on 28/09/15.
 */
public class PermissionVerifier implements Serializable {

    private DashboardBean dashboardBean;



    public boolean visible(String role)
    {
        if(dashboardBean != null && role != null)
        {
            ActorEO emp = dashboardBean.getAccount();

            if(emp != null)
            {
                if(role.toLowerCase().equals(emp.getRole().getName().toLowerCase()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }
}
