package com.degla.controllers;

import com.degla.db.models.*;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.*;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WorkflowValidator;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 18/05/15.
 */
public class BasicController implements BasicRestfulOperations {


    protected SystemService systemService;


    public BasicController() {

        try {
            systemService = SpringSystemBridge.services();

        } catch (Exception s) {
            s.printStackTrace();
        }

    }

    public List<RestfulFile> scanFiles(String query)
    {
        try
        {
            List<PatientFile> existingFiles = systemService.getFilesService().scanForFiles(query);

            if(existingFiles != null && existingFiles.size() > 0)
            {
                List<RestfulFile> availableFiles = new ArrayList<RestfulFile>();

                for(PatientFile file : existingFiles)
                {
                    RestfulFile restFile = new RestfulFile();
                    restFile.setCabinetId(file.getArchiveCabinet().getCabinetID());
                    restFile.setDescription(file.getDescription());
                    restFile.setFileNumber(file.getFileID());
                    restFile.setOperationDate(file.getCurrentStatus().getCreatedAt().getTime());
                    restFile.setShelfId(file.getShelfId());
                    restFile.setState(file.getCurrentStatus().getState().toString());
                    restFile.setTemporaryCabinetId(file.getCurrentStatus().getContainerId());

                    availableFiles.add(restFile);
                }


                return availableFiles;

            }else throw new Exception("Empty files");

        }catch (Exception s)
        {

            return new ArrayList<RestfulFile>();
        }
    }


    @Override
    public List<RestfulRequest> getNewRequests(String userName) {

        try {
            List<RestfulRequest> availableRequests = new ArrayList<RestfulRequest>();

            List<Request> requests = systemService.getRequestsManager().getNewRequestsFor(userName);

            if (requests != null) {
                for (Request current : requests) {
                    RestfulRequest request = new RestfulRequest();

                    request.setAppointment_Date(current.getAppointment_Date());
                    request.setAppointment_Type(current.getAppointment_Type());
                    request.setFileNumber(current.getFileNumber());
                    request.setPatientName(current.getPatientName());
                    request.setPatientNumber(current.getPatientNumber());
                    request.setUserName(current.getUserName());

                    availableRequests.add(request);
                }

                return availableRequests;
            } else return null;

        } catch (Exception s) {
            s.printStackTrace();
            return null;
        }


    }

    @Override
    public boolean updateFile(RestfulFile file, Employee emp) throws
            RecordNotFoundException,WorkflowOutOfBoundException{


        try {
            if (file.getState() != null && FileStates.valueOf(file.getState()) == FileStates.CHECKED_OUT)
            {
                //Step one : to select the request that contains that file number
                Request currentRequest = systemService.getRequestsManager()
                        .getSingleRequest(file.getFileNumber());

                //Step Two : Check to see if the file Exists
                PatientFile patientFile = systemService.getFilesService()
                        .getFileWithNumber(file.getFileNumber());

                if (patientFile != null) {

                    if(!WorkflowValidator.canProceed(patientFile.getCurrentStatus().getState(),
                            FileStates.valueOf(file.getState())))
                    {
                        String message =String.format("File: %s needs to be submitted by %s",
                                patientFile.getFileID(),String.format("%s %s", patientFile.getCurrentStatus().getOwner()
                                        .getFirstName(), patientFile.getCurrentStatus().getOwner().getLastName()));

                        throw new WorkflowOutOfBoundException(message);
                    }

                    this.addNewFileHistory(patientFile, file, emp);

                    boolean result = systemService.getFilesService().updateEntity(patientFile);

                    if (result)
                        return systemService.getRequestsManager().removeEntity(currentRequest);
                    else return false;

                } else {
                    //Create a new Patient File
                    PatientFile newPatientFile = new PatientFile();
                    //create a new file cabinet
                    ArchiveCabinet cabinet = new ArchiveCabinet();
                    cabinet.setCabinetID(file.getCabinetId());
                    cabinet.setCreationTime(new Date());
                    newPatientFile.setArchiveCabinet(cabinet);
                    newPatientFile.setCreationTime(new Date());
                    newPatientFile.setFileID(file.getFileNumber());
                    newPatientFile.setShelfId(file.getShelfId());
                    newPatientFile.setPatientName(currentRequest.getPatientName());
                    newPatientFile.setPatientNumber(currentRequest.getPatientNumber());
                    this.addNewFileHistory(newPatientFile, file, emp);

                    boolean result = systemService.getFilesService().addEntity(newPatientFile);

                    if (result) {
                        //now remove the current request
                        return systemService.getRequestsManager().removeEntity(currentRequest);

                    } else return false;

                }
            }else if (file.getState() != null &&
                    file.getState().equals(FileModelStates.MISSING.toString())){

                //Step one : to select the request that contains that file number
                Request currentRequest = systemService.getRequestsManager()
                        .getSingleRequest(file.getFileNumber());

               if(currentRequest != null)
               {
                   PatientFile missingFile = new PatientFile();
                   ArchiveCabinet cabinet = new ArchiveCabinet();
                   cabinet.setCreationTime(new Date());
                   cabinet.setCabinetID(file.getCabinetId());
                   missingFile.setArchiveCabinet(cabinet);
                   missingFile.setCreationTime(new Date());
                   missingFile.setPatientName(currentRequest.getPatientName());
                   missingFile.setPatientNumber(currentRequest.getPatientNumber());
                   missingFile.setFileID(file.getFileNumber());
                   this.addNewFileHistory(missingFile,file,emp);

                   boolean result = systemService.getFilesService().addEntity(missingFile);

                   if(result)
                       return systemService.getRequestsManager().removeEntity(currentRequest);
                   else return false;

               }else
               {
                   //Get the file by knowing its file number
                   PatientFile patientFile = systemService.getFilesService()
                           .getFileWithNumber(file.getFileNumber());

                   if(patientFile != null)
                   {
                       if(!(WorkflowValidator.canProceed(patientFile.getCurrentStatus().getState(),
                               FileStates.valueOf(file.getState()))))
                       {
                           String message =String.format("File: %s needs to be submitted by %s",
                                   patientFile.getFileID(),String.format("%s %s", patientFile.getCurrentStatus().getOwner()
                                           .getFirstName(), patientFile.getCurrentStatus().getOwner().getLastName()));

                           throw new WorkflowOutOfBoundException(message);
                       }

                       this.addNewFileHistory(patientFile, file, emp);

                       boolean result = systemService.getFilesService().updateEntity(patientFile);

                       return result;

                   }else return false;
               }


            } else {
                //Step Two : Check to see if the file Exists
                PatientFile patientFile = systemService.getFilesService()
                        .getFileWithNumber(file.getFileNumber());

                if (patientFile != null) {

                    if(!(WorkflowValidator.canProceed(patientFile.getCurrentStatus().getState(),
                            FileStates.valueOf(file.getState()))))
                    {
                        String message =String.format("File: %s needs to be submitted by %s",
                                patientFile.getFileID(),String.format("%s %s", patientFile.getCurrentStatus().getOwner()
                                        .getFirstName(), patientFile.getCurrentStatus().getOwner().getLastName()));

                        throw new WorkflowOutOfBoundException(message);
                    }

                    this.addNewFileHistory(patientFile, file, emp);

                    boolean result = systemService.getFilesService().updateEntity(patientFile);

                    return result;

                } else throw new RecordNotFoundException("Requested Patient File is not recorded");
            }


        } catch (EntityNotFoundException s) {
            throw new RecordNotFoundException("Requested File does not exists");
        }


    }

    private void addNewFileHistory(PatientFile patientFile, RestfulFile file, Employee emp) {

        FileHistory history = new FileHistory();
        history.setContainerId(file.getTemporaryCabinetId());
        history.setOwner(emp);
        FileStates state = FileStates.valueOf(file.getState().toString());
        history.setState(state);
        if(file.getOperationDate() == null)
            file.setOperationDate(new Date().getTime());

        history.setCreatedAt(new Date(file.getOperationDate()));
        history.setPatientFile(patientFile);
        patientFile.setCurrentStatus(history);
    }

    @Override
    public boolean updateFiles(List<RestfulFile> files, Employee emp) throws RecordNotFoundException,
            WorkflowOutOfBoundException {


            boolean finalResult = true;

            if (files != null && files.size() > 0) {
                for (RestfulFile file : files) {

                    finalResult &= this.updateFile(file, emp);
                }

                return true;

            }
            return false;

    }

    /**
     * This method will search for patient files based on their file numbers , patient numbers
     *
     * @param query - either the patient file number or patient number to search for
     * @return
     */
    @Override
    public List<RestfulFile> searchFiles(String query) {

        try {
            List<RestfulFile> availableFiles = new ArrayList<RestfulFile>();

            List<PatientFile> files = systemService.getFilesService().searchFiles(query);

            if (files != null && files.size() > 0) {
                for (PatientFile file : files) {
                    availableFiles.add(file.toRestfulFile());
                }

                return availableFiles;

            } else return null;


        } catch (Exception s) {
            s.printStackTrace();
            return null;
        }
    }
}
