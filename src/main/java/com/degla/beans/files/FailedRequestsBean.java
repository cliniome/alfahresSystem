package com.degla.beans.files;

import com.degla.db.models.Request;
import com.degla.utils.WebUtils;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 17/06/15.
 */
@ManagedBean(name="failedRequestsBean")
@SessionScoped
public class FailedRequestsBean implements Serializable {


    private List<Request> failedRequests;


    public void deleteRequestWithNumber(String requestId)
    {
        try
        {
            Request tempRequest = null;

            for(Request current : getFailedRequests())
            {
                if(current.getFileNumber().equals(requestId))
                {
                    tempRequest = current;
                    break;
                }
            }

            if(tempRequest != null)
                getFailedRequests().remove(tempRequest);

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void clearRequests(ActionEvent event)
    {
        try
        {

            this.setFailedRequests(new ArrayList<Request>());

            WebUtils.addMessage("Failed Requests have been deleted Successfully");

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    @PreDestroy
    public void onDestroy()
    {
        this.setFailedRequests(null);
    }

    public List<Request> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Request> failedRequests) {
        this.failedRequests = failedRequests;
    }
}
