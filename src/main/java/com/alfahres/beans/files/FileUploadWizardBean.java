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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by snouto on 15/05/15.
 */
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


    private FailedRequestsBean failedRequestsBean;




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


    @PreDestroy
    public void onDestroy()
    {
        this.setFailedRequests(new ArrayList<Request>());
    }


    public void onConfirmAndSubmit()
    {

        String message = "There are no New Requests";

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

                        req.setClinic_Doc_Code(key.getT_clinic_doc_code());
                        req.setClinicCode(key.getT_clinic_code());
                        req.setClinicName(key.getClinic_name());
                        req.setCsGroupCount(key.getCs_group_count());
                        req.setRequestingDocName(key.getDoc_name());
                        if(req.getFileNumber() == null || req.getFileNumber().length() <= 0)
                        {
                            getFailedRequests().add(req);
                            continue;
                        }

                        if(req.getPatientNumber() == null || req.getPatientNumber().length() <=0)
                        {
                            getFailedRequests().add(req);
                            continue;
                        }
                        String regex = "[0-9]+";
                        Pattern pattern = Pattern.compile(regex);
                        if(!pattern.matcher(req.getFileNumber()).matches())
                        {
                            //add the current request to the failed requests
                            getFailedRequests().add(req);
                            continue;
                        }

                        //get the new File Number structure
                        BarcodeUtils barcodeUtils = new BarcodeUtils(req.getFileNumber());

                        String patientFileNumber = req.getFileNumber();

                        if(!barcodeUtils.validateLength())
                        {
                            //that means the current request is not a valid request
                            getFailedRequests().add(req);
                            continue;
                        }

                        if(!barcodeUtils.isNewFileStructure())
                            patientFileNumber = barcodeUtils.getNewBarcodeStructure();

                        req.setFileNumber(patientFileNumber);


                       /* if(req.getAppointment_Date() != null && req.getFileNumber().equals("01-00710004"))
                        {
                            //make the request date to the start of the day
                            Date reqDate = req.getAppointment_Date();
                            reqDate = AlfahresDateUtils.getStartOfDay(reqDate);
                            req.setAppointment_Date(reqDate);
                        }*/

                        //Try to append the hours into the original request appointment date
                        try
                        {
                           /* if(req.getAppointment_time() != null && !req.getAppointment_time().isEmpty())
                            {
                                if(req.getAppointment_time().contains(":"))
                                {
                                    String[] splittedHours = req.getAppointment_time().split(":");
                                    int hour = Integer.parseInt(splittedHours[0]);
                                    int minute = Integer.parseInt(splittedHours[1]);

                                    Calendar calc = Calendar.getInstance();
                                    calc.setTime(req.getAppointment_Date());
                                    calc.set(Calendar.HOUR,hour);
                                    calc.set(Calendar.MINUTE,minute);
                                    req.setAppointment_Date(calc.getTime());
                                }

                            }*/

                        }catch (Exception s)
                        {
                            s.printStackTrace();

                        }

                        //check to see if that request number exists already in patient Files
                        //If it does not exists no problem.
                        //if it exists please check to see if its current state is not CHECKED_IN , SO ignore that file otherwise accept
                        PatientFile exists = systemService.getFilesService().getFileWithNumber(req.getFileNumber());

                        if(exists != null)
                        {
                            //check to see if that patient file is already archived
                            if(exists.getCurrentStatus() != null && exists.getCurrentStatus().getState() != FileStates.CHECKED_IN)
                            {
                                //add that as a transfer request
                                systemService.getTransferManager().addEntity(req.toTransferObject());
                                continue;
                            }
                        }


                        availableRequests.add(req);
                    }

                    //set the failed Requests
                    failedRequestsBean.setFailedRequests(getFailedRequests());



                }

            }

            //Before routing the Requests
            //Sort the requests based on appointment time in ascending order
            Collections.sort(availableRequests);
            //Create a temporary list
            List<Request> temporaryList = new ArrayList<Request>();
            List<Transfer> transfers = new ArrayList<Transfer>();

            for(Request current:availableRequests)
            {


                /*
                  1. Check for the exactness of the current request , if it is an exact match , don't add it at all to the collection and just continue
                  2. if it is not exact match , that means it is partial matching , so it might be a transfer
                  3. check for the exactness of the transfer , if it is not an exact transfer so add it to the transfer collection if it does not exist otherwise,continue

                 */

                Date todayDate = new Date();

                todayDate = AlfahresDateUtils.getEndOfDay(todayDate);

                if(todayDate.compareTo(current.getAppointment_Date()) < 1)
                {
                    current.setFailureReason("The request contains an old appointment date");
                    failedRequests.add(current);
                    continue;
                }


                /*
                    1. Check in the requests table ,if the same request number exists for the same clinic code in the same date,
                    so basically , that means a duplicate request , drop it , otherwise , check if it is for a different clinic code , so it is definitely a transfer
                    2. check if that transfer exactly was inserted before or not., if it was inserted before , drop it otherwise add it as a transfer

                 */
                boolean exact_request_exists = this.checkRequestExactness(current) || this.templistContainsExactly(current, temporaryList);

                if(exact_request_exists) continue;
                else
                {
                    //it could be a partial match
                    boolean partial_request = this.existsPartiallyinDB(current) || this.existsPartiallyInMemory(current,temporaryList);

                    if(partial_request)
                    {
                        //that means this is a transfer
                        boolean transfer_exists = systemService.getTransferManager().hasTransferBasedonClinicCode(current.getFileNumber(),
                                current.getClinicCode(),current.getAppointment_date_h(),current.getAppointment_time()) ||
                                this.transferListContains(current.clone().toTransferObject(),transfers);

                        if(!transfer_exists)
                            transfers.add(current.clone().toTransferObject());


                    }else
                    {
                        //that means it is definitely a new request
                        if(!this.templistContainsExactly(current, temporaryList))
                            temporaryList.add(current);
                    }
                }

                /*if(!this.templistContainsExactly(current,temporaryList) && !this.existsPartiallyinDB(current))
                {
                    temporaryList.add(current);
                }else
                {
                    boolean transfer_exists  = systemService.getTransferManager().hasTransferBasedonClinicCode(current.getFileNumber(),
                            current.getClinicCode(),current.getAppointment_date_h(),current.getAppointment_time()) ||
                            this.transferListContains(current.clone().toTransferObject(),transfers);

                    if(!transfer_exists)
                        transfers.add(current.clone().toTransferObject());
                }*/


            }


            //now route them to different employees
            systemService.getFileRouter().routeFiles(temporaryList);

            if(temporaryList != null && temporaryList.size() > 0)
            {
                boolean result = true;
                //insert all requests one by one
                for(Request currentRequest : temporaryList)
                {
                    try
                    {
                        //Take the clinic and try to add it if it is not exist
                        try
                        {
                            String clinicCode = currentRequest.getClinicCode();
                            if(clinicCode != null)
                                clinicCode = clinicCode.trim();

                            boolean exists = systemService.getClinicManager().clinicExists(clinicCode);

                            if(!exists)
                            {
                                //create a new clinic for it
                                Clinic newClinic = new Clinic(currentRequest.getClinicName(),clinicCode);

                                //add the clinic tomessage the data base
                                systemService.getClinicManager().addEntity(newClinic);

                            }

                        }catch (Exception s)
                        {
                            System.out.println("During Saving the Clinic " + currentRequest.getFileNumber());
                            s.printStackTrace();
                        }

                       try
                       {
                           boolean stepResult = systemService.getRequestsManager().addEntity(currentRequest);
                           result = result && stepResult;

                       }catch (Exception s)
                       {
                           System.out.print("During saving the Request " + currentRequest.getFileNumber());
                           s.printStackTrace();
                       }

                    }catch (Exception s)
                    {
                        System.out.println("Problem Saving Request " + currentRequest.getFileNumber());
                        s.printStackTrace();
                        continue;
                    }
                }



                if(result)
                {
                    message = "Requests have been submitted Successfully";
                }


            }else
            {
                //WebUtils.addMessage("There are no New Requests or transfers in the Database");
            }

            //check to see if the transfers already contains data
            if(transfers != null && transfers.size() > 0)
            {
                for(Transfer currentTransfer : transfers)
                {
                    try
                    {
                        systemService.getTransferManager().addEntity(currentTransfer);

                    }catch (Exception s)
                    {
                        s.printStackTrace();
                    }
                }
            }else
                message +=" With No Transfers";


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

    private boolean existsPartiallyInMemory(Request current, List<Request> temporaryList) {

        boolean result= false;

        for(Request req : temporaryList)
        {
            if(req.getFileNumber().equals(current.getFileNumber()))
            {
                result = true;
                break;
            }
        }

        return result;

    }

    private boolean checkRequestExactness(Request current)
    {
        try
        {
            if(current == null) return false;

            return systemService.getRequestsManager().requestExistsExactlyInDB(current);

        }catch (Exception s)
        {
            return false;
        }
    }

    private boolean existsPartiallyinDB(Request current) {

        try
        {
            if(current == null) return false;

            return systemService.getRequestsManager().requestExists(current.getFileNumber());

        }catch (Exception s)
        {
            return false;
        }
    }

    private boolean transferListContains(Transfer transfer,List<Transfer> availableTransfers)
    {
        if(availableTransfers == null || availableTransfers.size() <= 0 ) return false;
        boolean result = false;

        for(Transfer current : availableTransfers)
        {
            try
            {
                if(current.exactMatch(transfer))
                {
                    result = true;
                    break;
                }

            }catch (Exception s)
            {
                s.printStackTrace();
                break;
            }
        }

        return result;
    }

    private boolean templistContainsExactly(Request current, List<Request> availableRequests) {

        if(availableRequests == null || availableRequests.size()<=0) return false;

        boolean result  = false;

        for(Request req : availableRequests)
        {
            try
            {
                if(current.isExact(req))
                {
                    result = true;
                    break;
                }

            }catch (Exception s)
            {
                s.printStackTrace();
                continue;
            }
        }

        return result;

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

    public List<Request> getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(List<Request> failedRequests) {
        this.failedRequests = failedRequests;
    }

    public FailedRequestsBean getFailedRequestsBean() {
        return failedRequestsBean;
    }

    public void setFailedRequestsBean(FailedRequestsBean failedRequestsBean) {
        this.failedRequestsBean = failedRequestsBean;
    }
}