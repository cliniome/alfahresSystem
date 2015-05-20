package com.degla.beans;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by snouto on 20/05/15.
 */
@ManagedBean(name="requestsBean")
@ViewScoped
public class RequestsBean implements Serializable {


    private SystemService systemService;

    private GenericLazyDataModel<Request> availableRequests;



    public RequestsBean()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            this.initRequests();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public long getTotalCount()
    {
        if(getAvailableRequests() == null || getAvailableRequests().getRowCount() <=0) return 0;
        else return getAvailableRequests().getRowCount();
    }

    private void initRequests()
    {
        setAvailableRequests(new GenericLazyDataModel<Request>(systemService.getRequestsManager()));
    }


    public GenericLazyDataModel<Request> getAvailableRequests() {
        return availableRequests;
    }

    public void setAvailableRequests(GenericLazyDataModel<Request> availableRequests) {
        this.availableRequests = availableRequests;
    }
}
