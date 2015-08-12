package com.alfahres.beans.files;

import com.degla.dao.FilesDAO;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.restful.models.FileModelStates;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileStateUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 19/06/15.
 */
public class ShowFilesBean implements Serializable {


    private SystemService systemService;
    private String chosenState = FileStates.MISSING.toString();
    private List<SelectItem> items;
    private List<PatientFile> patientFiles;
    private String displayMessage = "Missing";
    private FileStates defaultState = FileStates.MISSING;

    private ViewHelperBean viewHelper;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setItems(new ArrayList<SelectItem>());
            setPatientFiles(new ArrayList<PatientFile>());

            FacesContext context = FacesContext.getCurrentInstance();
            String stateString = context.getExternalContext().getRequestParameterMap().get("state");

            if(stateString != null && !stateString.isEmpty())
            {
                setDefaultState(FileStates.valueOf(stateString));
                viewHelper.setCurrentState(stateString);
            }

            defaultState = FileStates.valueOf(viewHelper.getCurrentState());

            FileStateUtils utils = new FileStateUtils(defaultState);

            this.setDisplayMessage(utils.getReadableState());

            patientFiles = systemService.getFilesService().getFilesWithFileStates(getDefaultState());

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
                FileStates currentState = defaultState;
                FilesDAO filesManager  = systemService.getFilesService();
                filesManager.setQueryState(currentState);
                setPatientFiles(new ArrayList<PatientFile>());

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




    public List<PatientFile> getPatientFiles() {
        return patientFiles;
    }

    public void setPatientFiles(List<PatientFile> patientFiles) {
        this.patientFiles = patientFiles;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public FileStates getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(FileStates defaultState) {
        this.defaultState = defaultState;
    }


    public ViewHelperBean getViewHelper() {
        return viewHelper;
    }

    public void setViewHelper(ViewHelperBean viewHelper) {
        this.viewHelper = viewHelper;
    }
}
