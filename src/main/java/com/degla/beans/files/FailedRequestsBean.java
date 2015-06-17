package com.degla.beans.files;

import com.degla.db.models.Request;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
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
