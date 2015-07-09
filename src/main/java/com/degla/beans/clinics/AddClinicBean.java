package com.degla.beans.clinics;

import com.degla.db.models.Clinic;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by snouto on 05/06/15.
 */
@ManagedBean(name = "addClinic")
@ViewScoped
public class AddClinicBean {


    private SystemService systemService;

    private String clinicName;
    private String clinicCode;



    @PostConstruct
    public void initBean()
    {
        try
        {
            systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();

        }
    }


    public void addClinic()
    {
        try
        {

            if(systemService.getClinicManager().clinicExists(getClinicCode()))
            {
                WebUtils.addMessage("Current Clinic Exists, Please try to add different Clinic");
                return;
            }
            Clinic newClinic = new Clinic(this.getClinicName(),this.getClinicCode());
            //now try to add the current clinic
            boolean result = systemService.getClinicManager().addEntity(newClinic);

            if(result)
            {
                WebUtils.addMessage(String.format("Clinic %s with clinic code %s" +
                        " was added successfully",this.getClinicName(),this.getClinicCode()));
                this.clear();

            }else
            {
                WebUtils.addMessage(String.format("There was a problem adding" +
                        " the %s clinic",this.getClinicName()));
            }


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    private void clear() {

        this.setClinicCode("");
        this.setClinicName("");
    }


    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }
}
