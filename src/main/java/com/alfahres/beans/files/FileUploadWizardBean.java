package com.alfahres.beans.files;

import com.degla.db.models.*;
import com.degla.exceptions.BarcodeFormatException;
import com.degla.restful.utils.AlfahresDateUtils;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.WebUtils;
import com.degla.utils.xml.BatchRequestDetails;
import com.degla.utils.xml.PatientFileReader;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FileUploadEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.model.SelectItem;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by snouto on 15/05/15.
 */
public class FileUploadWizardBean implements Serializable {





    public static final String NO_ENTRY_FILE = "Not Entered/No File";

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
    private List<Appointment> failedRequests;
    private FailedRequestsBean failedRequestsBean;




    @PostConstruct
    public void onInit()
    {
        try {
            systemService = SpringSystemBridge.services();
            setGeneratedFields(new ArrayList<SelectItem>());
            getGeneratedFields().add(new SelectItem());
            this.setFailedRequests(new ArrayList<Appointment>());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @PreDestroy
    public void onDestroy()
    {
        this.setFailedRequests(new ArrayList<Appointment>());
    }


    public void onConfirmAndSubmit()
    {

        String message = "There are no New Requests";

        try
        {
            PatientFileReader fileReader = new PatientFileReader();
            Map<BatchRequestDetails,List<Appointment>> requests = fileReader.buildRequests(this);

            List<Appointment> availableRequests = new ArrayList<Appointment>();

            if(requests == null || requests.size() <= 0 ) return;

            for(BatchRequestDetails key : requests.keySet())
            {
                //get all requests
                List<Appointment> values = requests.get(key);

                if(values != null && values.size() > 0)
                {
                    for(Appointment req : values)
                    {

                        req.setClinic_Doc_Code(key.getT_clinic_doc_code());
                        req.setClinicCode(key.getT_clinic_code());
                        req.setClinicName(key.getClinic_name());
                        req.setCsGroupCount(key.getCs_group_count());
                        req.setRequestingDocName(key.getDoc_name());

                        if(req.getFileNumber() == null || req.getFileNumber().length() <= 0)
                        {
                            getFailedRequestsBean().getFailedRequests().add(req);
                            continue;
                        }

                        if(req.getPatientNumber() == null || req.getPatientNumber().length() <=0)
                        {
                            getFailedRequestsBean().getFailedRequests().add(req);
                            continue;
                        }
                        String regex = "[0-9]+";
                        Pattern pattern = Pattern.compile(regex);
                        if(!pattern.matcher(req.getFileNumber()).matches())
                        {
                            //add the current request to the failed requests
                            getFailedRequestsBean().getFailedRequests().add(req);
                            continue;
                        }

                        //get the new File Number structure
                        BarcodeUtils barcodeUtils = new BarcodeUtils(req.getFileNumber());

                        String patientFileNumber = req.getFileNumber();

                        if(!barcodeUtils.validateLength())
                        {
                            //that means the current request is not a valid request
                            getFailedRequestsBean().getFailedRequests().add(req);
                            continue;
                        }

                        if(!barcodeUtils.isNewFileStructure())
                            patientFileNumber = barcodeUtils.getNewBarcodeStructure();

                        req.setFileNumber(patientFileNumber);


                        PatientFile currentPatientFile = systemService.getFilesService().getFileWithNumber(req.getFileNumber());

                        if(currentPatientFile != null)
                        {
                            if(currentPatientFile.getCurrentStatus().getState() != FileStates.CHECKED_IN)
                                req.setWatchList(true);
                        }



                        if(req.getFileCurrentLocation() != null && req.getFileCurrentLocation().trim().toLowerCase().contains(NO_ENTRY_FILE.trim().toLowerCase()))
                        {
                            req.setFailureReason(NO_ENTRY_FILE);
                            failedRequestsBean.getFailedRequests().add(req);
                            continue;
                        }
                        availableRequests.add(req);
                    }
                    //set the failed Requestssynchronized
                    //failedRequestsBean.setFailedRequests(getFailedRequests());



                }
            }
            //Before routing the Requests
            //Sort the requests based on appointment time in ascending order
            Collections.sort(availableRequests);
            boolean result = true;
            //now route them to different employees
            systemService.getFileRouter().routeFiles(availableRequests);

            for(Appointment current:availableRequests)
            {
                try
                {
                    String clinicCode = current.getClinicCode();
                    if(clinicCode != null)
                        clinicCode = clinicCode.trim();

                    boolean exists = systemService.getClinicManager().clinicExists(clinicCode);

                    if(!exists)
                    {
                        //create a new clinic for it
                        Clinic newClinic = new Clinic(current.getClinicName(),clinicCode);
                        //add the clinic tomessage the data base
                        systemService.getClinicManager().addEntity(newClinic);
                    }
                }catch (Exception s)
                {
                    System.out.println("During Saving the Clinic " + current.getFileNumber());
                    s.printStackTrace();
                }
                //check for the current request , did we see it before in any file history or not.
                //Boolean existsInHistory = systemService.getFileHistoryDAO().appointmentExistsInHistory(current);
                boolean appointmentExists = systemService.getAppointmentManager().appointmentExistsBefore(current);

                if (appointmentExists)
                {
                    current.setFailureReason("Duplicate Entry - Appointment has been added before");
                    getFailedRequestsBean().getFailedRequests().add(current);
                    continue;
                }else
                {
                    try
                    {
                        //check for transfers
                        if(this.checkTransfers(current)){
                            current.setWatchList(true);
                        }

                        boolean stepResult = systemService.getAppointmentManager().addEntity(current);
                        result = result && stepResult;

                    }catch (Exception s)
                    {
                        System.out.print("During saving the Request " + current.getFileNumber());
                        s.printStackTrace();
                    }

                }
            }


            if(result)
            {
                message = "Requests have been submitted Successfully";
            }

            WebUtils.addMessage(message);

        }catch(Exception s)
        {
            s.printStackTrace();
            if(s instanceof BarcodeFormatException)
            {
                WebUtils.addMessage(s.getMessage());
            }else
                WebUtils.addMessage(s.getMessage());
        }
    }

    private boolean checkTransfers(Appointment current) throws Exception {

        //check to see if you have an appointment today for the current file

        if(systemService == null)
            systemService = SpringSystemBridge.services();

        return systemService.getAppointmentManager().hasTransfer(current.getFileNumber(),current.getAppointment_Date());
    }

    public void onFileUploadListener(FileUploadEvent event)
    {

        List<Employee> activeKeepers = systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString()
                ,true);

        if(activeKeepers == null || activeKeepers.size() <=0)
        {
            WebUtils.addMessage("There are no Active Keepers to assign the uploaded Requests , Please try to " +
                    "add Keepers in the system before proceeding with the upload.");
            return;
        }

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

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF8"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF8"));


            /*int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputstream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
*/

            String line = null;

            while((line=reader.readLine()) != null)
            {
                line = line.trim().replaceAll("\u00A0"," ");
                writer.write(line);
            }

           writer.flush();
            reader.close();
            writer.close();



            //Here is our Preprocessing step after uploading the file successfully
           // this.preprocessFile(fileFullPath);

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



    public FailedRequestsBean getFailedRequestsBean() {
        return failedRequestsBean;
    }

    public void setFailedRequestsBean(FailedRequestsBean failedRequestsBean) {
        this.failedRequestsBean = failedRequestsBean;
    }

    public List<Appointment> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Appointment> failedRequests) {
        this.failedRequests = failedRequests;
    }
}
