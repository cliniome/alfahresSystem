package com.alfahres.beans.clinics;

import com.degla.db.models.Appointment;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.ClinicsSlipCreator;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snouto on 09/10/15.
 */
public class ClinicsSlipsBean implements Serializable {

    private Date fromDate = new Date();
    private Date toDate = new Date();

    private SystemService systemService;





    @PostConstruct
    public void onInit(){


        try
        {
            this.systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }


    public void chooseSlips()
    {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal",true);
        options.put("closable",true);
        options.put("resizable", false);
        options.put("contentHeight", 350);
        RequestContext.getCurrentInstance().openDialog("selectSlips",options,null);
    }






    public StreamedContent downloadClinicsSlips()
    {
        try
        {
            if(systemService == null) systemService = SpringSystemBridge.services();


            List<Appointment> appointments = systemService.getAppointmentManager().getAppointmentsForDateRange(getFromDate(),getToDate());

            if(appointments != null)
            {
                RequestContext.getCurrentInstance().closeDialog(appointments);

                //begin creating the full path for the document and the outputstream to write the document to
                String fullPath = systemService.getSystemSettings().getSystemUploadPath();
                fullPath = String.format("%s%s.pdf",fullPath,String.valueOf(new Date().getTime()));
                FileOutputStream outputStream = new FileOutputStream(fullPath);

                //Get the document
                ClinicsSlipCreator slipCreator = new ClinicsSlipCreator();
                Document pdfSlips = slipCreator.getClinicsSlip(appointments, outputStream);

                //now access the file for download
                StreamedContent streamedContent = new DefaultStreamedContent(new FileInputStream(fullPath));


                return streamedContent;


            }


            return null;

        }catch (Exception s)
        {
            s.printStackTrace();

            return null;
        }
    }
}
