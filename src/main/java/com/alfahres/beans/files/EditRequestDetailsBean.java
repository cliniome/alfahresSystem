package com.alfahres.beans.files;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 17/06/15.
 */
public class EditRequestDetailsBean implements Serializable {

    private List<Request> failedRequests;
    private Request fileRequest;
    private SystemService systemService;

    private String oldNumber;
    private String fileNumber;
    private String patientName;
    private String patientNumber;

    private FailedRequestsBean failedRequestsBean;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            //Access the selected Request  from the flash scope
            List<Request> failedRequests =  failedRequestsBean.getFailedRequests();

            if(failedRequests != null)
            {
                //access the passed in parameter from the query String
                String requestId = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("id");

                if(requestId != null)
                {
                    //get the request
                    Request selectedRequest = this.getRequest(requestId);
                    this.setFileRequest(selectedRequest.clone());

                    this.setFileNumber(selectedRequest.getFileNumber());
                    this.setOldNumber(selectedRequest.getFileNumber());
                    this.setPatientName(selectedRequest.getPatientName());
                    this.setPatientNumber(selectedRequest.getPatientNumber());
                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    private Request getRequest(String requestID) {

        try
        {
            Request tempRequest = null;

            for(Request current : failedRequestsBean.getFailedRequests())
            {
                if(current != null && current.getFileNumber().equals(requestID))
                {
                    tempRequest = current;
                    break;
                }
            }

            return tempRequest;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    @PreDestroy
    public void onDestroy()
    {
        this.setFileRequest(null);
    }


    public void onSubmit(ActionEvent event)
    {
        try
        {
            if(getFileRequest() == null)
            {
                Request oldRequest = getRequest(this.getOldNumber());

                this.setFileRequest(oldRequest.clone());
            }
            //Do the submission in here
            if(getFileRequest() != null)
            {
                Request currentRequest = getFileRequest();
                String fileNumber = getFileNumber();
                BarcodeUtils utils = new BarcodeUtils(fileNumber);

                if(utils.isNewFileStructure())
                {
                    WebUtils.addMessage("Please insert The number without \"01-\"");
                    return;
                }

                currentRequest.setFileNumber(utils.getNewBarcodeStructure());
                currentRequest.setPatientName(getPatientName());
                currentRequest.setPatientNumber(getPatientNumber());

                //now try to route the current request to a specific employee
                List<Request> tempRequests = new ArrayList<Request>();
                tempRequests.add(currentRequest);

                //route the Requests
                systemService.getFileRouter().routeFiles(tempRequests);

                //now get the request
                Request request = tempRequests.get(0);

                //now save it
                boolean result = systemService.getRequestsManager().addEntity(request);

                if(result)
                {
                    //delete it from the failed Requests
                    failedRequestsBean.deleteRequestWithNumber(this.getOldNumber());

                    WebUtils.addMessage("Request has been Updated and Saved successfully");
                }else
                    WebUtils.addMessage("There was a problem updating and saving the current Request , Please contact your system administrator");

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }





    public Request getFileRequest() {
        return fileRequest;
    }

    public void setFileRequest(Request fileRequest) {
        this.fileRequest = fileRequest;
    }

    public List<Request> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Request> failedRequests) {
        this.failedRequests = failedRequests;
    }

    public FailedRequestsBean getFailedRequestsBean() {
        return failedRequestsBean;
    }

    public void setFailedRequestsBean(FailedRequestsBean failedRequestsBean) {
        this.failedRequestsBean = failedRequestsBean;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
    }
}
