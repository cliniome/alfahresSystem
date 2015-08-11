package com.alfahres.beans;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.ExcelFileBuilder;
import com.degla.utils.GenericLazyDataModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 20/05/15.
 */
public class RequestsBean implements Serializable {


    private SystemService systemService;

    private GenericLazyDataModel<Request> availableRequests;

    private StreamedContent excelFile;

    public static final int MAX_RESULTS = 20;

    private DashboardBean dashboardBean;


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


    public void onPagination(PageEvent event)
    {
        getDashboardBean().setCurrentPageNumber(event.getPage());
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

    public StreamedContent getExcelFile() {
        try
        {
            //Get all the files in here
            excelFile = getFileContents();

            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();

           /* String fileName = String.format("%s%s.xls","patientFiles",getCurrentPage());
            response.addHeader("Content-Disposition",String.format("attachment; filename=%s.pdf;",
                    fileName));*/

            return excelFile;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }

    }


    private StreamedContent getFileContents() throws Exception
    {

        ExcelFileBuilder fileBuilder = new ExcelFileBuilder(systemService);

        Workbook wb = fileBuilder.buildExcelFile();
        if(wb == null) return null;

        //then write it
        String fullPath = systemService.getSystemSettings().getSystemUploadPath();
        fullPath = String.format("%s%s.xls",fullPath,String.valueOf(new Date().getTime()));
        FileOutputStream outputStream = new FileOutputStream(fullPath);
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
        //now read that file
        FileInputStream inputStream = new FileInputStream(fullPath);
        return  new DefaultStreamedContent(inputStream,"application/vnd.ms-excel",String.format("%s-%s.xls",
                "PatientFiles",String.valueOf(new Date().getTime())));

    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }



    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }
}
