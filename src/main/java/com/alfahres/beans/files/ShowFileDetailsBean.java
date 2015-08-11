package com.alfahres.beans.files;

import com.degla.db.models.FileHistory;
import com.degla.db.models.PatientFile;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by snouto on 10/06/15.
 */
public class ShowFileDetailsBean {

    private PatientFile file;
    private SystemService systemService;
    private List<FileHistory> fileHistories;


    @PostConstruct
    public void onInit()
    {
        try
        {
            setSystemService(SpringSystemBridge.services());
            //get the file based on its file number
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String fileNumber = facesContext.getExternalContext().getRequestParameterMap()
                    .get("fileNumber");

            if(fileNumber != null)
            {
                //get the Patient File
                this.setFile(getSystemService().getFilesService().getFileWithNumber(fileNumber));
                //get the current file history
                List<FileHistory> histories = getSystemService().getFileHistoryDAO().getFileHistory(getFile());
                this.setFileHistories(histories);
                //Sort them
                if(this.getFileHistories() != null && this.getFileHistories().size() > 0)
                {
                    //now sort them in descending order based on the createdAt flag
                    Collections.sort(this.getFileHistories(), new Comparator<FileHistory>() {
                        @Override
                        public int compare(FileHistory first, FileHistory second) {

                            return first.getCreatedAt().compareTo(second.getCreatedAt());
                        }
                    });
                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public PatientFile getFile() {
        return file;
    }

    public void setFile(PatientFile file) {
        this.file = file;
    }

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public List<FileHistory> getFileHistories() {
        return fileHistories;
    }

    public void setFileHistories(List<FileHistory> fileHistories) {
        this.fileHistories = fileHistories;
    }
}
