package com.alfahres.beans.files;

import com.degla.db.models.Appointment;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.ExcelFileBuilder;
import com.degla.utils.WebUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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


    public StreamedContent getExcelFile(){


        try
        {

            SystemService systemService = SpringSystemBridge.services();
            //Create the Excel File Builder
            ExcelFileBuilder builder = new ExcelFileBuilder();

            Workbook wb = builder.createFailedRequests(this.getFailedRequests());

            if(wb == null) return null;

            String fullPath = systemService.getSystemSettings().getSystemUploadPath();
            fullPath = String.format("%s%s.xls",fullPath,String.valueOf(new Date().getTime()));
            FileOutputStream outputStream = new FileOutputStream(fullPath);
            wb.write(outputStream);
            outputStream.flush();
            WebUtils.addMessage("Failed Requests have been deleted Successfully");

            outputStream.close();
            //now read that file
            FileInputStream inputStream = new FileInputStream(fullPath);
            return  new DefaultStreamedContent(inputStream,"application/vnd.ms-excel",String.format("%s-%s.xls",
                    "Failed-Appointments",String.valueOf(new Date().getTime())));

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }


    public void clearRequests(ActionEvent event)
    {
        try
        {

            this.setFailedRequests(new ArrayList<Appointment>());

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

        if(this.failedRequests == null) this.failedRequests = new ArrayList<Appointment>();

        return failedRequests;
    }

    public void setFailedRequests(List<Appointment> failedRequests){
       this.failedRequests = failedRequests;
    }
}
