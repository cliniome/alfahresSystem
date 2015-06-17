package com.degla.beans.files;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * Created by snouto on 17/06/15.
 */
@ManagedBean(name="editRequestBean")
@RequestScoped
public class EditRequestDetailsBean implements Serializable {

    private Request request;
    private SystemService systemService;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            //Access the request Number

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onEdit(ActionEvent event)
    {
        try
        {

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
