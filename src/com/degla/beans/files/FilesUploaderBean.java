package com.degla.beans.files;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by snouto on 15/05/15.
 */
@ManagedBean(name="filesuploader")
@ViewScoped
public class FilesUploaderBean implements Serializable {


    public String onFlowListener(FlowEvent event)
    {
        if(event.getNewStep() == null)
            return "upload";
        else return event.getNewStep();
    }

    public void onFileUploadListener(FileUploadEvent event)
    {
        System.out.println("Uploading file");
    }

}
