package com.alfahres.beans.files;

import com.degla.db.models.Appointment;
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
public class FailedRequestsBean implements Serializable {


    private List<Appointment> failedRequests;


    public void deleteRequestWithNumber(String requestId)
    {
        try
        {
            Appointment tempRequest = null;

            for(Appointment current : getFailedRequests())
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

            this.setFailedRequests(new ArrayList<Appointment>());

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


    public List<Appointment> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Appointment> failedRequests) {
        this.failedRequests = failedRequests;
    }
}
