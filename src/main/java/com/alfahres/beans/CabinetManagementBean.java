package com.alfahres.beans;

import com.degla.db.models.ArchiveCabinet;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Date;

/**
 * Created by snouto on 10/05/2015.
 */
public class CabinetManagementBean {

    private SystemService systemService;
    private String cabinetID;
    private String description;
    private String location;
    private GenericLazyDataModel<ArchiveCabinet> cabinets;

    private ArchiveCabinet updateableCabinet;


    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setCabinets(new GenericLazyDataModel<ArchiveCabinet>(systemService.getCabinetsService()));

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onEditCabinet(ActionEvent event)
    {
        try
        {
            FacesContext context = FacesContext.getCurrentInstance();
            String cabinetID = context.getExternalContext().getRequestParameterMap().get("cabinetID");
            if(cabinetID != null)
            {
                //get the cabinet from the database
                ArchiveCabinet updateableCab = systemService.getCabinetsService().getCabinetByID(cabinetID);
                this.setUpdateableCabinet(updateableCab);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onUpdateCabinet(ActionEvent event)
    {
        try
        {
            if(this.getUpdateableCabinet() != null)
            {
                //now try to update the current updateable cabinet
                boolean result = systemService.getCabinetsService().updateEntity(this.getUpdateableCabinet());

                if(result)
                {
                    WebUtils.addMessage("The Cabinet has been updated Successfully");

                }else
                {
                    WebUtils.addMessage("There was a problem updating the current Cabinet,Contact your System Support");
                }


            }else
            {
                WebUtils.addMessage("System can't update an empty cabinet,Contact your System Support");
            }

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onAddCabinet(ActionEvent event)
    {
        try
        {
            //adding new Cabinet
            ArchiveCabinet cabinet = new ArchiveCabinet();
            cabinet.setDescription(this.getDescription());
            cabinet.setCabinetID(this.getCabinetID());
            cabinet.setLocation(this.getLocation());
            cabinet.setCreationTime(new Date());
            //now try to add the new cabinet to the database
            boolean result = systemService.getCabinetsService().addEntity(cabinet);
            if(result)
            {
                this.clear();
                WebUtils.addMessage("The archive Cabinet has been added Successfully");
            }else
            {
                WebUtils.addMessage("There was a problem adding the cabinet");
            }


        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }

    private void clear() {

        this.setLocation("");
        this.setCabinetID("");
        this.setDescription("");
        this.setUpdateableCabinet(null);
    }

    public String getCabinetID() {
        return cabinetID;
    }

    public void setCabinetID(String cabinetID) {
        this.cabinetID = cabinetID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GenericLazyDataModel<ArchiveCabinet> getCabinets() {
        return cabinets;
    }

    public void setCabinets(GenericLazyDataModel<ArchiveCabinet> cabinets) {
        this.cabinets = cabinets;
    }

    public ArchiveCabinet getUpdateableCabinet() {
        return updateableCabinet;
    }

    public void setUpdateableCabinet(ArchiveCabinet updateableCabinet) {
        this.updateableCabinet = updateableCabinet;
    }
}
