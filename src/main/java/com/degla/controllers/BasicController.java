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

    public List<RestfulFile> scanFiles(String query,List<FileStates> states)
    {
        try
        {
            List<PatientFile> existingFiles = systemService.getFilesService().scanForFiles(query, states);

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
                    restFile.setPatientName(file.getPatientName());
                    restFile.setPatientNumber(file.getPatientNumber());
                    restFile.setAppointmentDate(file.getCurrentStatus().getAppointment_Hijri_Date());
                    restFile.setAppointmentDateH(file.getCurrentStatus().getAppointment_Hijri_Date());
                    restFile.setAppointmentMadeBy(file.getCurrentStatus().getAppointment_Made_by());
                    restFile.setAppointmentTime(file.getCurrentStatus().getAppointment_Hijri_Date());
                    restFile.setBatchRequestNumber(file.getCurrentStatus().getBatchRequestNumber());
                    restFile.setAppointmentType(file.getCurrentStatus().getAppointmentType());
                    restFile.setClinicCode(file.getCurrentStatus().getClinicCode());
                    restFile.setClinicDocCode(file.getCurrentStatus().getClinicDocCode());
                    restFile.setClinicDocName(file.getCurrentStatus().getClinicDocName());
                    restFile.setClinicName(file.getCurrentStatus().getClinicName());


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
                    request.setAppointment_Date(current.getAppointment_Date());
                    request.setAppointment_Type(current.getCf_appointment_type());
                    request.setAppointmentDate(current.getAppointment_Date());
                    request.setAppointmentDateH(current.getAppointment_date_h());
                    request.setAppointmentMadeBy(current.getAppointment_made_by());
                    request.setAppointmentTime(current.getAppointment_time());
                    request.setAppointmentType(current.getCf_appointment_type());
                    request.setBatchRequestNumber(current.getBatchRequestNumber());
                    request.setClinicCode(current.getClinicCode());
                    request.setClinicDocCode(current.getClinic_Doc_Code());
                    request.setClinicDocName(current.getRequestingDocName());
                    request.setClinicName(current.getClinicName());
                    request.setState(FileStates.NEW.toString());

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
    public boolean updateFile(RestfulFile file, Employee emp)
            throws RecordNotFoundException, WorkflowOutOfBoundException {

        try
        {
            //First check to see if the request is found
            Request foundRequest = systemService.getRequestsManager().getRequestByBatchNumber(
                    file.getFileNumber(),file.getBatchRequestNumber());

            if(foundRequest != null)
            {
                //That means the current restful file is just checked out from the keeper
                //Create a new Patient File
                PatientFile newPatientFile = new PatientFile();
                //create a new file cabinet
                ArchiveCabinet cabinet = null;

                cabinet = systemService.getCabinetsService().getCabinetByID(file.getCabinetId());

                if(cabinet != null)
                {
                    newPatientFile.setArchiveCabinet(cabinet);

                }else
                {
                    cabinet = new ArchiveCabinet();
                    cabinet.setCabinetID(file.getCabinetId());
                    cabinet.setCreationTime(new Date());
                    newPatientFile.setArchiveCabinet(cabinet);
                }

                newPatientFile.setCreationTime(new Date());
                newPatientFile.setFileID(file.getFileNumber());
                newPatientFile.setShelfId(file.getShelfId());
                newPatientFile.setPatientName(foundRequest.getPatientName());
                newPatientFile.setPatientNumber(foundRequest.getPatientNumber());
                this.addNewFileHistory(newPatientFile, file, emp);

                boolean result = systemService.getFilesService().addEntity(newPatientFile);

                if (result) {
                    //now remove the current request
                    return systemService.getRequestsManager().removeEntity(foundRequest);

                } else return false;

            }else
            {
                //that means the current request is not found
                //it means it is not the first time for that file in the system
                //Get the file by knowing its file number
                PatientFile patientFile = systemService.getFilesService()
                        .getFileWithNumber(file.getFileNumber());

                if(patientFile == null) throw new RecordNotFoundException("There is something wrong " +
                        "in the system , Please contact your system administrator");

                //now update the current Patient File with the restful File
                patientFile.updateWithRestful(file);
                //then add new file History to that
                this.addNewFileHistory(patientFile,file,emp);

                //finally update the current patient file
                return systemService.getFilesService().updateEntity(patientFile);


            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
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
        history.setAppointment_Hijri_Date(file.getAppointmentDateH());
        history.setAppointment_Made_by(file.getAppointmentMadeBy());
        history.setBatchRequestNumber(file.getBatchRequestNumber());
        history.setAppointmentType(file.getAppointmentType());
        history.setClinicCode(file.getClinicCode());
        history.setClinicDocCode(file.getClinicDocCode());
        history.setClinicDocName(file.getClinicDocName());
        history.setClinicName(file.getClinicName());
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
