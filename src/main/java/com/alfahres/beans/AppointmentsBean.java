package com.alfahres.beans;

import com.degla.db.models.Appointment;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.ExcelFileBuilder;
import com.degla.utils.AppointmentsLazyDataModel;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by snouto on 20/05/15.
 */
public class AppointmentsBean implements Serializable {


    private SystemService systemService;

    private AppointmentsLazyDataModel<Appointment> availableAppointments;

    private AppointmentsLazyDataModel<Appointment> watchListAppointments;

    private StreamedContent excelFile;

    public static final int MAX_RESULTS = 20;

    private DashboardBean dashboardBean;


    public AppointmentsBean()
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

    public boolean filterByDate(Object value, Object filter, Locale locale) {

        if( filter == null ) {
            return true;
        }

        if( value == null ) {
            return false;
        }

        return DateUtils.truncatedEquals((Date) filter, (Date) value, Calendar.DATE);
    }


    public void onPagination(PageEvent event)
    {

        getDashboardBean().setCurrentPageNumber(event.getPage());
    }

    public long getTotalCount()
    {
        if(getAvailableAppointments() == null || getAvailableAppointments().getRowCount() <=0) return 0;
        else return getAvailableAppointments().getRowCount();
    }

    public long getTotalWatchList()
    {
        if(getWatchListAppointments() == null || getWatchListAppointments().getRowCount() <= 0 ) return 0;
        else return getWatchListAppointments().getRowCount();
    }

    private void initRequests()
    {
        setAvailableAppointments(new AppointmentsLazyDataModel<Appointment>(systemService.getAppointmentManager(),false));
        setWatchListAppointments(new AppointmentsLazyDataModel<Appointment>(systemService.getAppointmentManager(),true));
    }




    public StreamedContent getExcelFile(boolean watchList) {
        try
        {
            //Get all the files in here
            excelFile = getFileContents(watchList);

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


    private StreamedContent getFileContents(boolean watchList) throws Exception
    {

        ExcelFileBuilder fileBuilder = new ExcelFileBuilder(systemService,watchList);

        Workbook wb = fileBuilder.buildExcelNow();
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


    public AppointmentsLazyDataModel<Appointment> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(AppointmentsLazyDataModel<Appointment> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }

    public AppointmentsLazyDataModel<Appointment> getWatchListAppointments() {
        return watchListAppointments;
    }

    public void setWatchListAppointments(AppointmentsLazyDataModel<Appointment> watchListAppointments) {
        this.watchListAppointments = watchListAppointments;
    }
}
