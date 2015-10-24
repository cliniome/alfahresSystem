package com.alfahres.beans.stats;

import com.alfahres.beans.dashboard.DashboardMetrics;
import com.alfahres.beans.files.ViewHelperBean;
import com.degla.dao.utils.SearchSettings;
import com.degla.db.models.Appointment;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.ExcelFileBuilder;
import com.degla.utils.GenericLazyDataModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by snouto on 13/08/15.
 */
public class ViewStatisticsBean implements Serializable {


    public static final int DISPLAY_ALL = 0;
    public static final int DISPLAY_BY_APPOINTMENT=1;
    private SystemService systemService;
    private GenericLazyDataModel<PatientFile> files;
    private ViewHelperBean viewHelper;
    private FileStates[] states;
    private int displayType;







    @PostConstruct
    public void onInit()
    {
        try
        {

            setSystemService(SpringSystemBridge.services());

            if(getViewHelper().getCurrentState() != null &&
                    getViewHelper().getCurrentState().length() > 0 &&
                    !getViewHelper().getCurrentState().isEmpty() &&
                    getViewHelper().getAppointmentDate() != null)
            {
                FileStates currentState = FileStates.valueOf(getViewHelper().getCurrentState());
                systemService.getFilesService().setQueryState(currentState);
                systemService.getFilesService().setAppointmentDate(getViewHelper().getAppointmentDate());

            }



            SearchSettings searchSettings = new SearchSettings();
            searchSettings.setAppointmentDate(getViewHelper().getAppointmentDate());

            if(getViewHelper().getCurrentState() != null && !getViewHelper().getCurrentState().isEmpty())
               searchSettings.setStatus(FileStates.valueOf(getViewHelper().getCurrentState()));


            searchSettings.setType(this.getDisplayType());
            searchSettings.setWatchlist(getViewHelper().isInWatchList());

            systemService.getPrintFilesDAO().setSearchSettings(searchSettings);
            getViewHelper().setPrintSearchSettings(searchSettings);

            files = new GenericLazyDataModel<PatientFile>(systemService.getPrintFilesDAO());
            states = FileStates.values();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onReset(ActionEvent event)
    {
        if(getViewHelper() != null)
        {
            getViewHelper().setAppointmentDate(null);
            getViewHelper().setInWatchList(false);
            getViewHelper().setCurrentState(null);
        }


        //finally do a default search
        onSearch(event);
    }

    @PreDestroy
    public void onDestroy()
    {
        if(systemService != null)
        {
            systemService.getFilesService().setAppointmentDate(null);
            systemService.getFilesService().setQueryState(null);
            this.getViewHelper().setAppointmentDate(null);

        }
    }

    public String onLoadParameters(){


        this.clear();

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String,String> parameters = context.getExternalContext().getRequestParameterMap();

        if(parameters != null && parameters.size() > 0)
        {
            String type = parameters.get("type");
            String status = parameters.get("status");

            String inpatient = parameters.get("inpatient");

            if(type != null && status != null)
            {
                this.setDisplayType(Integer.parseInt(type));
                getViewHelper().setCurrentState(status);
            }

            if(inpatient == null)
            {
                getViewHelper().setInpatient(false);
            }else
            {
                boolean inPatientResult = (Integer.parseInt(inpatient)) == 1 ? true :false;
                getViewHelper().setInpatient(inPatientResult);
            }

        }

        //Do the search again
        onSearch(null);
        return "";
    }

    private void clear() {

        getViewHelper().setAppointmentDate(null);
        getViewHelper().setInWatchList(false);
    }


    public void onSearch(ActionEvent event)
    {
        try
        {
            if(systemService == null)
                systemService = SpringSystemBridge.services();

            SearchSettings searchSettings = new SearchSettings();
            searchSettings.setAppointmentDate(getViewHelper().getAppointmentDate());

            if(getDisplayType() == DISPLAY_ALL)
                searchSettings.setStatus(FileStates.valueOf(getViewHelper().getCurrentState()));
            else
                searchSettings.setStatus(FileStates.valueOf(getViewHelper().getSecondState()));

            searchSettings.setType(this.getDisplayType());
            searchSettings.setWatchlist(getViewHelper().isInWatchList());
            getViewHelper().setPrintSearchSettings(searchSettings);
            searchSettings.setInpatient(getViewHelper().isInpatient());


           /* systemService.getFilesService().setQueryState(FileStates.valueOf(getViewHelper().getCurrentState()));
            systemService.getFilesService().setAppointmentDate(getViewHelper().getAppointmentDate());
            systemService.getFilesService().setInWatchList(getViewHelper().isInWatchList());
            systemService.getFilesService().setAvailableAppointments(systemService.getAppointmentManager().getAppointmentsForDate(getViewHelper().getAppointmentDate()));*/

            systemService.getPrintFilesDAO().setSearchSettings(searchSettings);

            if(searchSettings.isWatchlist())
                systemService.getPrintFilesDAO().setAvailableAppointments(systemService.getAppointmentManager().getAppointmentsForDate(getViewHelper().getAppointmentDate()));



            GenericLazyDataModel<PatientFile> filterableModel = new GenericLazyDataModel<PatientFile>(systemService.getPrintFilesDAO());
            filterableModel.setFilterable(true);
            this.setFiles(filterableModel);



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public StreamedContent getExcelFile()
    {
        try
        {

            ExcelFileBuilder builder = new ExcelFileBuilder(systemService);

            systemService.getPrintFilesDAO().setSearchSettings(getViewHelper().getPrintSearchSettings());

            Workbook wb = null;

            if(getViewHelper().getAppointmentDate() == null)
                wb = builder.extractOnly(getViewHelper().getPrintSearchSettings().getStatus().toString(),
                        systemService.getPrintFilesDAO().getPaginatedWrappedData());
            else
                wb = builder.extractOnly(getViewHelper().getAppointmentDate(),
                        systemService.getPrintFilesDAO().getPaginatedWrappedData());

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


        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public GenericLazyDataModel<PatientFile> getFiles() {
        return files;
    }

    public long totalCount()
    {
        if(getFiles() != null)
            return getFiles().getRowCount();
        else return 0;
    }

    public void setFiles(GenericLazyDataModel<PatientFile> files) {
        this.files = files;
    }

    public ViewHelperBean getViewHelper() {
        return viewHelper;
    }

    public void setViewHelper(ViewHelperBean viewHelper) {
        this.viewHelper = viewHelper;
    }

    public FileStates[] getStates() {
        return states;
    }

    public void setStates(FileStates[] states) {
        this.states = states;
    }


    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }


}
