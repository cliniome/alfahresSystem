package com.degla.beans.files;

import com.degla.db.models.PatientFile;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 10/06/15.
 */
@ManagedBean(name="fileManagementBean")
@ViewScoped
public class FileManagementBean {

    public static final int FILENUMBER = 1;
    public static final int PATIENT_NUMBER = 2;
    public static final int PATIENT_NAME=3;
    public static final int BATCH_NUMBER=4;

    private String choice;
    private String query;
    private List<PatientFile> files;

    private SystemService systemService;


    @PostConstruct
    public void initBean()
    {
        try
        {
            files = new ArrayList<PatientFile>();
            systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getQuery() {
        return query;
    }


    public void setQuery(String query) {
        this.query = query;
    }

    public boolean emptyFiles()
    {
        if(files == null || files.size()<=0) return true;
        else return false;
    }

    public boolean emptyQuery()
    {
        if(this.getChoice() == null || this.getQuery() == null)
            return true;
        else return false;
    }


    public void doSearch(ActionEvent event)
    {
        try
        {
            if(!this.emptyQuery())
            {
                int choosedNumber = Integer.parseInt(this.getChoice());

                switch (choosedNumber)
                {
                    case FILENUMBER:
                    {
                        this.files = systemService.getFilesService().getFilesWithNumber(this.getQuery());
                    }
                    break;
                    case PATIENT_NAME:
                    {
                        this.files = systemService.getFilesService()
                                .getFilesWithPatientName(this.getQuery());
                    }
                    break;
                    case PATIENT_NUMBER:
                    {
                        this.files = systemService.getFilesService()
                                .getFilesWithPatientNumber(this.getQuery());
                    }
                    break;
                    case BATCH_NUMBER:
                    {
                        this.files = systemService.getFilesService()
                                .getFilesWithBatchNumber(this.getQuery());
                    }
                    break;
                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    public List<PatientFile> getFiles() {
        return files;
    }

    public void setFiles(List<PatientFile> files) {
        this.files = files;
    }
}
