package com.degla.beans;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sun.misc.OSEnvironment;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 20/05/15.
 */
@ManagedBean(name="requestsBean")
@SessionScoped
public class RequestsBean implements Serializable {


    private SystemService systemService;

    private GenericLazyDataModel<Request> availableRequests;

    private StreamedContent excelFile;

    private int currentPage = 0;

    public static final int MAX_RESULTS = 20;





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
        this.setCurrentPage(event.getPage());
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

            return excelFile;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }

    }


    private StreamedContent getFileContents() throws Exception
    {
        Workbook wb = new HSSFWorkbook();

        //Create a new Sheet
        Sheet currentSheet = wb.createSheet("Files");
        //Create the header
        Row headerRow = currentSheet.createRow(0);
        Cell fileNumber  =headerRow.createCell(0);
        fileNumber.setCellValue("File Number");
        Cell patientNumber = headerRow.createCell(1);
        patientNumber.setCellValue("Patient Number");

        //Get all Requests in the database
        List<Request> pageRequests =  systemService.getRequestsManager().getPaginatedResults(getCurrentPage() * MAX_RESULTS
                ,MAX_RESULTS);

        if(pageRequests != null && pageRequests.size() > 0)
        {
            for(int i=0;i<pageRequests.size() ;i++)
            {
                //create a row
                Row contentRow = currentSheet.createRow(i+1);
                //Create the first Cell
                Cell fileNumberCell = contentRow.createCell(0);
                fileNumberCell.setCellFormula(pageRequests.get(i).getFileNumber());
                //Create the patient Number Cell
                Cell patientNumberCell = contentRow.createCell(1);
                patientNumberCell.setCellValue(pageRequests.get(i).getPatientNumber());
            }

            //then write it
            String fullPath = systemService.getSystemSettings().getSystemUploadPath();
            fullPath = String.format("%s%s.xls",fullPath,new Date().toString());

            FileOutputStream outputStream = new FileOutputStream(fullPath);

            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();


            //now read that file
            FileInputStream inputStream = new FileInputStream(fullPath);


            return  new DefaultStreamedContent(inputStream,"application/vnd.ms-excel",String.format("%s-%s.xls",
                    "PatientFiles",String.valueOf(currentPage)));

        }

        return null;

    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
