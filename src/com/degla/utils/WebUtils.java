package com.degla.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Created by snouto on 08/05/2015.
 */
public class WebUtils {

    public static void addMessage(String msg)
    {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
