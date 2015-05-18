package com.degla.beans;

import com.degla.db.models.RoleEO;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

/**
 * Created by snouto on 10/05/2015.
 */
@ManagedBean(name="roleBean")
@ViewScoped
public class RoleManagementBean {

    private String description;
    private String displayName;
    private String name;

    private SystemService systemService;


    @PostConstruct
    public void onInit()
    {
        try {
            systemService = SpringSystemBridge.services();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onAddRole (ActionEvent event)
    {
        try
        {
            RoleEO role = new RoleEO(this.getName(),this.getDisplayName(),this.getDescription());
            //now try to add the new role in here
            boolean result = systemService.getRoleService().addEntity(role);

            if(result)
            {
                this.clear();
                WebUtils.addMessage("The Role has been added Successfully");
            }else
            {
                WebUtils.addMessage("There was a problem adding the new Role, Contact System Support.");
            }


        }catch(Exception s)
        {

        }
    }

    private void clear()
    {
        this.setName("");
        this.setDescription("");
        this.setDisplayName("");
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
