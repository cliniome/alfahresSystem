package com.alfahres.beans.css;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

/**
 * Created by snouto on 15/05/15.
 */
public class AlfahresCssBean implements Serializable {

    private String currentPageName;


    public String setActive(String name)
    {
        if(getCurrentPageName() != null && getCurrentPageName().equals(name))
            return "active";
        else return "";
    }

    public String getCurrentPageName() {
        return currentPageName;
    }

    public void setCurrentPageName(String currentPageName) {
        this.currentPageName = currentPageName;
    }
}
