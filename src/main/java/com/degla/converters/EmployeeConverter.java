package com.degla.converters;

import com.degla.db.models.Employee;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by snouto on 22/05/15.
 */
@FacesConverter("com.degla.converter.EmployeeConverter")
public class EmployeeConverter implements Converter {


    private SystemService systemService;

    public EmployeeConverter()
    {
        this.init();
    }

    private void init() {

        try
        {
            systemService = SpringSystemBridge.services();


        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {

        try
        {
            Employee emp = systemService.getEmployeeService().getEntity(Integer.parseInt(s));

            return emp;

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
             if(o != null && (o instanceof Employee))
                 return String.valueOf(((Employee)o).getId());
            else return null;

        }catch (Exception s)
        {

            return null;
        }

    }
}
