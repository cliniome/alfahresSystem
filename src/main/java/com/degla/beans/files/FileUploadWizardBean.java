package com.degla.beans.files;

import com.degla.db.models.Request;
import com.degla.exceptions.BarcodeFormatException;
import com.degla.exceptions.RequestException;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.WebUtils;
import com.degla.utils.xml.BatchRequestDetails;
import com.degla.utils.xml.PatientFileReader;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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
    private List<Request> failedRequests;



    @PostConstruct
    public void onInit()
    {
        try {
            systemService = SpringSystemBridge.services();
            setGeneratedFields(new ArrayList<SelectItem>());
            getGeneratedFields().add(new SelectItem());
            this.setFailedRequests(new ArrayList<Request>());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void onConfirmAndSubmit()
    {
        try
        {
            PatientFileReader fileReader = new PatientFileReader();
            Map<BatchRequestDetails,List<Request>> requests = fileReader.buildRequests(this);

            List<Request> availableRequests = new ArrayList<Request>();

            if(requests == null || requests.size() <= 0 ) return;

            for(BatchRequestDetails key : requests.keySet())
            {
                //get all requests
                List<Request> values = requests.get(key);

                if(values != null && values.size() > 0)
                {
                    for(Request req : values)
                    {

                        String regex = "[0-9]+";
                        Pattern pattern = Pattern.compile(regex);
                        if(!pattern.matcher(req.getFileNumber()).matches())
                        {
                            //add the current request to the failed requests
                            getFailedRequests().add(req);
                            continue;
                        }
                        req.setClinic_Doc_Code(key.getT_clinic_doc_code());
                        req.setClinicCode(key.getT_clinic_code());
                        req.setClinicName(key.getClinic_name());
                        req.setCsGroupCount(key.getCs_group_count());
                        req.setRequestingDocName(key.getDoc_name());
                        //get the new File Number structure
                        BarcodeUtils barcodeUtils = new BarcodeUtils(req.getFileNumber());
                        req.setFileNumber(barcodeUtils.getNewBarcodeStructure());

                        availableRequests.add(req);
                    }

                    //add failed requests to the flash variable
                    FacesContext.getCurrentInstance().getExternalContext()
                            .getFlash().putNow("failedRequests",getFailedRequests());
                }

            }

            //now route them to different employees
            systemService.getFileRouter().routeFiles(availableRequests);

            if(requests != null && requests.size() > 0)
            {
                boolean result = true;
                //insert all requests one by one
                for(Request currentRequest : availableRequests)
                {
                    boolean stepResult = systemService.getRequestsManager().addEntity(currentRequest);
                    result = result && stepResult;
                }

                if(result)
                    WebUtils.addMessage("Requests have been submitted successfully");
            }else
            {
                WebUtils.addMessage("There was a problem inserting new requests, Contact System Administrator");
            }

        }catch(Exception s)
        {
            s.printStackTrace();
            if(s instanceof BarcodeFormatException)
            {
                WebUtils.addMessage(s.getMessage());
            }else
                WebUtils.addMessage("There was a problem inserting new requests, Contact System Administrator");
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
                PatientFileReader fileReader = new PatientFileReader();
                List<String> fields = fileReader.readPatientFile(writtenFile);

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
          // getWizard().setStep("mapping");

           onConfirmAndSubmit();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String containsField(String fieldName)
    {
        if(fieldName.equals(this.getAppointment_Date())) return "appointment_Date";
        else if(fieldName.equals(this.getAppointment_Type())) return "appointment_Type";
        /*else if (fieldName.equals(this.getClinic_Doc_Code())) return "clinic_Doc_Code";
        else if(fieldName.equals(this.getClinicCode())) return "clinicCode";*/
        else if(fieldName.equals(this.getFileCurrentLocation())) return "fileCurrentLocation";
        else if(fieldName.equals(this.getFileNumber())) return "fileNumber";
        else if(fieldName.equals(this.getPatientName())) return "patientName";
        else if(fieldName.equals(this.getPatientNumber())) return "patientNumber";
        else if(fieldName.equals(this.getUserName())) return "userName";
        else return null;
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



            //Here is our Preprocessing step after uploading the file successfully
            this.preprocessFile(fileFullPath);

            return fileFullPath;



        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void preprocessFile(String fileFullPath) {

        try
        {
            File inputFile = new File(fileFullPath);

            if(inputFile.exists() && inputFile.canRead())
            {
                //now begin the reading process
                StringBuffer fileContents = new StringBuffer();

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));

                String line = null;

                while((line = reader.readLine()) != null)
                {
                    fileContents.append(line);
                }

                //Now let us see this in action now , the system has been deployed successfully on my local application server
                //Let us open it in the browser.

                //now the actual pre-processing happens
                //Here is the replacement code , i am replacing all occurrences of non-breaking space
                //in the file contents after reading it
                String newContents = fileContents.toString().trim().replaceAll("\u00A0"," ");

                //now delete that file
                //Deleting the actual file with non-breaking space
                inputFile.delete();

                //now create the file again
                //Then creating the new one without the non-breaking space
                File outputFile = new File(fileFullPath);

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

                writer.write(newContents);

                writer.flush();
                writer.close();


            }

        }catch (Exception s)
        {
            s.printStackTrace();
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

    public List<Request> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Request> failedRequests) {
        this.failedRequests = failedRequests;
    }
}
