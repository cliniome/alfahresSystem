package com.degla.beans.files;

import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import com.degla.utils.xml.PatientFileReader;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by snouto on 15/05/15.
 */
@ManagedBean(name="fileWizard")
@ViewScoped
public class FileUploadWizardBean implements Serializable {

    private String patientNumber;
    private String fileNumber;
    private String patientName;
    private String appointment_Type;
    private String appointment_Date;
    private String userName;
    private String fileCurrentLocation;
    private String clinicCode;
    private String clinic_Doc_Code;
    private List<SelectItem> generatedFields;
    private SystemService systemService;
    private Wizard wizard;
    private String uploadedFile;


    @PostConstruct
    public void onInit()
    {
        try {
            systemService = SpringSystemBridge.services();
            setGeneratedFields(new ArrayList<SelectItem>());
            getGeneratedFields().add(new SelectItem());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void onConfirmAndSubmit()
    {
        try
        {
            WebUtils.addMessage("Requests have been submitted successfully");
            System.out.println("Adding Requests Done.");

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }


    public String onFlowListener(FlowEvent event)
    {
        if(event.getNewStep() == null)
            return "upload";
        else return event.getNewStep();
    }

    public void onFileUploadListener(FileUploadEvent event)
    {
        String fileName = UUID.randomUUID().toString()+"."+systemService.getSystemSettings().getUploadableFileExtension();
        try {
            String writtenFile = uploadFile(fileName,event.getFile().getInputstream());


            if(writtenFile != null)
            {
                this.setUploadedFile(writtenFile);
                //read the file
                List<String> fields = PatientFileReader.readPatientFile(writtenFile);

                if(fields != null && fields.size() > 0)
                {
                    for(String field : fields)
                    {
                        SelectItem item = new SelectItem(field,field);

                        this.getGeneratedFields().add(item);
                    }
                }
            }

            //move to the next step in the wizard
            getWizard().setStep("mapping");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean containsField(String fieldName)
    {
        if(fieldName.equals(this.getAppointment_Date())) return true;
        else if(fieldName.equals(this.getAppointment_Type())) return true;
        else if (fieldName.equals(this.getClinic_Doc_Code())) return true;
        else if(fieldName.equals(this.getClinicCode())) return true;
        else if(fieldName.equals(this.getFileCurrentLocation())) return true;
        else if(fieldName.equals(this.getFileNumber())) return true;
        else if(fieldName.equals(this.getPatientName())) return true;
        else if(fieldName.equals(this.getPatientNumber())) return true;
        else if(fieldName.equals(this.getUserName())) return true;
        else return false;
    }

    private String uploadFile(String fileName, InputStream inputstream) {

        String fileFullPath = systemService.getSystemSettings().getSystemUploadPath()+fileName;

        try {

            FileOutputStream outputStream = new FileOutputStream(new File(fileFullPath));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputstream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            inputstream.close();
            outputStream.flush();
            outputStream.close();

            return fileFullPath;



        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointment_Type() {
        return appointment_Type;
    }

    public void setAppointment_Type(String appointment_Type) {
        this.appointment_Type = appointment_Type;
    }

    public String getAppointment_Date() {
        return appointment_Date;
    }

    public void setAppointment_Date(String appointment_Date) {
        this.appointment_Date = appointment_Date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileCurrentLocation() {
        return fileCurrentLocation;
    }

    public void setFileCurrentLocation(String fileCurrentLocation) {
        this.fileCurrentLocation = fileCurrentLocation;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getClinic_Doc_Code() {
        return clinic_Doc_Code;
    }

    public void setClinic_Doc_Code(String clinic_Doc_Code) {
        this.clinic_Doc_Code = clinic_Doc_Code;
    }


    public List<SelectItem> getGeneratedFields() {
        return generatedFields;
    }

    public void setGeneratedFields(List<SelectItem> generatedFields) {
        this.generatedFields = generatedFields;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    public String getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
}
