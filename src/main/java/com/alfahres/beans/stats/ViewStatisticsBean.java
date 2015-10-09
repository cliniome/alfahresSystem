package com.alfahres.beans.stats;

import com.alfahres.beans.files.ViewHelperBean;
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
import javax.faces.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 13/08/15.
 */
public class ViewStatisticsBean implements Serializable {



    private SystemService systemService;

    private GenericLazyDataModel<PatientFile> files;

    private ViewHelperBean viewHelper;

    private FileStates[] states;





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

            files = new GenericLazyDataModel<PatientFile>(systemService.getFilesService());
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


    public void onSearch(ActionEvent event)
    {
        try
        {
            if(systemService == null)
                systemService = SpringSystemBridge.services();

            systemService.getFilesService().setQueryState(FileStates.valueOf(getViewHelper().getCurrentState()));
            systemService.getFilesService().setAppointmentDate(getViewHelper().getAppointmentDate());
            systemService.getFilesService().setInWatchList(getViewHelper().isInWatchList());
            systemService.getFilesService().setAvailableAppointments(systemService.getAppointmentManager().getAppointmentsForDate(getViewHelper().getAppointmentDate()));


            GenericLazyDataModel<PatientFile> filterableModel = new GenericLazyDataModel<PatientFile>(systemService.getFilesService());
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

            Workbook wb = builder.extractOnly(getViewHelper().getAppointmentDate(),
                    systemService.getFilesService().getPaginatedWrappedData());

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


}
