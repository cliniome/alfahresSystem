package com.degla.utils;

import java.io.Serializable;

/**
 * Created by snouto on 15/05/15.
 */
public class SystemSettings implements Serializable {

    private String systemUploadPath;

    public String getSystemUploadPath() {
        return systemUploadPath;
    }

    public void setSystemUploadPath(String systemUploadPath) {
        this.systemUploadPath = systemUploadPath;
    }
}
