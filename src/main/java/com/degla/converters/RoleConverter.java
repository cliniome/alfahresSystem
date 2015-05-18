package com.degla.converters;

import com.degla.db.models.RoleEO;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by snouto on 08/05/2015.
 */
@FacesConverter(value="com.degla.converters.RoleConverter")
public class RoleConverter implements Converter {

    private SystemService systemService;


    public RoleConverter()
    {
        this.init();
    }

    private void init(){

        try
        {
            systemService = SpringSystemBridge.services();

        }catch(Exception s)
        {
            s.printStackTrace();
        }

    }
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {

        if(systemService == null) return null;
        else
        {
            RoleEO role = systemService.getRoleService().getEntity(Integer.parseInt(s));

            return role;
        }

    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {

        if(o == null ||!(o instanceof  RoleEO)) return null;
        else return String.valueOf(((RoleEO)o).getId());
    }
}
