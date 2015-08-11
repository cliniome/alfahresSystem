package com.alfahres.beans.clinics;

import com.degla.db.models.Clinic;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Created by snouto on 05/06/15.
 */
public class ClinicManagementBean {



    private static final String EDIT_CLINIC = "EDIT_CLINIC";
    private SystemService systemService;
    private Clinic selectedClinic;
    private GenericLazyDataModel<Clinic> availableClinics;

    @PostConstruct
    public void initBean()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setAvailableClinics(new GenericLazyDataModel<Clinic>(systemService.getClinicManager()));

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onShowClinic()
    {
        try
        {
            //get the clinic id
            FacesContext context = FacesContext.getCurrentInstance();
            String clinicId = context.getExternalContext().getRequestParameterMap().get("clinicId");

            if(clinicId != null)
            {
                //Get the clinic by its id , from the database
                Clinic selected = systemService.getClinicManager().getEntity(Integer.parseInt(clinicId));

                if(selected != null)
                    this.setSelectedClinic(selected);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public String onSelectClinic()
    {
        try
        {
            if(this.getSelectedClinic() == null)
                throw new Exception("There was a System Error , Please try again !");


            return EDIT_CLINIC;



        }catch (Exception s)
        {
            s.printStackTrace();
            return "";
        }
    }


    public void onUpdateClinic()
    {
        try{

            if(this.getSelectedClinic() == null) throw new Exception("There was a system Error," +
                    " Please try again!");

            if(systemService ==null)
                systemService = SpringSystemBridge.services();


            //now begin the update process
            boolean result = systemService.getClinicManager().updateEntity(this.getSelectedClinic());

            if(result)
            {
                WebUtils.addMessage(String.format("Clinic %s was updated Successfully.",
                        this.getSelectedClinic().getClinicName()));

            }else
            {
                WebUtils.addMessage(String.format("There was a problem updating the %s clinic ,Please contact " +
                        "Your system administrator",this.getSelectedClinic().getClinicName()));
            }


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    private void clear() {

        this.setSelectedClinic(null);

    }


    public GenericLazyDataModel<Clinic> getAvailableClinics() {
        return availableClinics;
    }

    public void setAvailableClinics(GenericLazyDataModel<Clinic> availableClinics) {
        this.availableClinics = availableClinics;
    }

    public Clinic getSelectedClinic() {
        return selectedClinic;
    }

    public void setSelectedClinic(Clinic selectedClinic) {
        this.selectedClinic = selectedClinic;
    }
}
