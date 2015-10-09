package com.degla.converters;

import com.degla.db.models.Appointment;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by snouto on 08/10/15.
 */

@FacesConverter("com.degla.converter.AppointmentConverter")
public class AppointmentConverter implements Converter {

    private SystemService systemService;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try
        {

            if(systemService == null)
                systemService = SpringSystemBridge.services();


            Appointment currentAppointment = systemService.getAppointmentManager().getEntity(Integer.parseInt(s));

            return currentAppointment;

        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {

        try
        {

            if(o != null && (o instanceof Appointment))
            {
                return String.valueOf(((Appointment)o).getId());
            }else return null;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }
}
