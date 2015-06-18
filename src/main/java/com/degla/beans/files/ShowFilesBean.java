package com.degla.beans.files;

import com.degla.dao.FilesDAO;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileStateUtils;
import com.degla.utils.GenericLazyDataModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 19/06/15.
 */
@ManagedBean(name="showFilesBean")
@SessionScoped
public class ShowFilesBean implements Serializable {


    private SystemService systemService;
    private String chosenState;
    private List<SelectItem> items;
    private GenericLazyDataModel<PatientFile> patientFiles;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setItems(new ArrayList<SelectItem>());
            FilesDAO filesDAO = systemService.getFilesService();
            patientFiles = new GenericLazyDataModel<PatientFile>(filesDAO);

            //Load selectable items
            this.loadSelectableItems();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void filter(ActionEvent event)
    {
        try
        {
            if(chosenState != null && chosenState.length() > 0)
            {
                FileStates currentState = FileStates.valueOf(chosenState);
                FilesDAO filesManager  = systemService.getFilesService();
                filesManager.setQueryState(currentState);
                patientFiles = new GenericLazyDataModel<PatientFile>(filesManager);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    private void loadSelectableItems()
    {
        try
        {
            for(FileStates state : FileStates.values())
            {
                FileStateUtils utils = new FileStateUtils(state);

                SelectItem item = new SelectItem(state.toString(),utils.getReadableState());
                getItems().add(item);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public String getChosenState() {
        return chosenState;
    }

    public void setChosenState(String chosenState) {
        this.chosenState = chosenState;
    }

    public List<SelectItem> getItems() {
        return items;
    }

    public void setItems(List<SelectItem> items) {
        this.items = items;
    }

    public GenericLazyDataModel<PatientFile> getPatientFiles() {
        return patientFiles;
    }

    public void setPatientFiles(GenericLazyDataModel<PatientFile> patientFiles) {
        this.patientFiles = patientFiles;
    }
}
