package com.degla.controllers;

import com.degla.db.models.*;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.*;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileStateUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 18/05/15.
 */
public class BasicController implements BasicRestfulOperations {


    private SystemService systemService;


    public BasicController() {

        try {
            setSystemService(SpringSystemBridge.services());

        } catch (Exception s) {
            s.printStackTrace();
        }

    }

    public List<RestfulFile> scanIndividualFiles(String query, List<FileStates> states)
    {
        try
        {
            List<PatientFile> existingFiles = getSystemService().getFilesService().
                    scanForIndividualFiles(query, states);

            if(existingFiles != null && existingFiles.size() > 0)
            {
                List<RestfulFile> availableFiles = new ArrayList<RestfulFile>();

                for(PatientFile file : existingFiles)
                {
                    RestfulFile restFile = file.toRestfulFile();


                    availableFiles.add(restFile);
                }


                return availableFiles;

            }else throw new Exception("Empty files");

        }catch (Exception s)
        {

            return new ArrayList<RestfulFile>();
        }
    }

    public List<RestfulFile> scanFiles(String query,List<FileStates> states)
    {
        try
        {
            List<PatientFile> existingFiles = getSystemService().getFilesService().scanForFiles(query, states);

            if(existingFiles != null && existingFiles.size() > 0)
            {
                List<RestfulFile> availableFiles = new ArrayList<RestfulFile>();

                for(PatientFile file : existingFiles)
                {
                    RestfulFile restFile = file.toRestfulFile();


                    availableFiles.add(restFile);
                }


                return availableFiles;

            }else throw new Exception("Empty files");

        }catch (Exception s)
        {

            return new ArrayList<RestfulFile>();
        }
    }




    public List<RestfulRequest> selectRequestsWithDate(String username , String date)
    {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat();
            Date chosenDate = DateUtils.parseDate(date, SpringSystemBridge.services().getDatePatternsBean().getDatePatterns().toArray(new String[]{}));
            List<RestfulRequest> availableRequests = new ArrayList<RestfulRequest>();

            List<Appointment> appointments = getSystemService().getAppointmentManager().selectAppointmentsByDate(username, chosenDate);

            Collections.sort(appointments);

            List<Appointment> tempAppointments = new ArrayList<Appointment>();

            if (appointments != null) {


                for (Appointment current : appointments) {

                    if(tempAppointments.contains(current))
                        continue;
                    else tempAppointments.add(current);


                    PatientFile foundFile = getSystemService().getFilesService().getFileWithNumber(current.getFileNumber());

                    RestfulRequest request = current.toRestfulRequest();

                    if(foundFile != null)
                        request.setShelfId(foundFile.getShelfId());

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
    public boolean updateFile(RestfulFile file , Employee emp)
    {
        try
        {
            Appointment appointment = getSystemService().getAppointmentManager().getEntity(file.getAppointmentId());

            boolean patientFileExists = getSystemService().getFilesService().fileExists(file.getFileNumber());

            if(!patientFileExists) //that means it is a new file
            {
                PatientFile newPatientFile = new PatientFile();
                ArchiveCabinet cabinet = null;

                cabinet = getSystemService().getCabinetsService().getCabinetByID(file.getCabinetId());

                if(cabinet != null)
                    newPatientFile.setArchiveCabinet(cabinet);
                else
                {
                    cabinet = new ArchiveCabinet();
                    cabinet.setCabinetID(file.getCabinetId());
                    cabinet.setCreationTime(new Date());

                    //save the cabin into the database
                    getSystemService().getCabinetsService().addEntity(cabinet);

                    cabinet = getSystemService().getCabinetsService().getCabinetByID(cabinet.getCabinetID());

                    newPatientFile.setArchiveCabinet(cabinet);
                }

                newPatientFile.setCreationTime(new Date());
                newPatientFile.setFileID(file.getFileNumber());
                newPatientFile.setShelfId(file.getShelfId());
                newPatientFile.setPatientName(appointment.getPatientName());
                newPatientFile.setPatientNumber(appointment.getPatientNumber());





                //Add the new file History
                this.addNewHistory(file,newPatientFile,emp,appointment);

                boolean result = getSystemService().getFilesService().addEntity(newPatientFile);

                if (result) {
                    //now remove the current request
                    appointment.setActive(false);

                    return getSystemService().getAppointmentManager().updateEntity(appointment);

                } else return false;

            }else
            {
                //that means the patient file exists already
                PatientFile patientFile = getSystemService().getFilesService()
                        .getFileWithNumber(file.getFileNumber());


                //Check to see preemptive Ejection of the patient file from inpatient processing area to MR Room.

                this.checkPreemptiveEjection(patientFile,file);



                if(patientFile == null) throw new RecordNotFoundException("There is something wrong " +
                        "in the system , Please contact your system administrator");

                long serverTimeStamp = patientFile.getCurrentStatus().getCreatedAt().getTime();


                if(file.getOperationDate() != null &&
                        serverTimeStamp > file.getOperationDate()) return true;

                //now update the current Patient File with the restful File
                patientFile.updateWithRestful(file);

                appointment.setActive(false);
                getSystemService().getAppointmentManager().updateEntity(appointment);

               boolean result = this.addHistoryToExistingPatientFile(patientFile,file,emp,appointment);


                if(result)
                {
                    //Deactive the current appointment
                    appointment.setActive(false);
                    getSystemService().getAppointmentManager().updateEntity(appointment);

                    return getSystemService().getFilesService().updateEntity(patientFile);

                }else return false;


            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    private void checkPreemptiveEjection(PatientFile patientFile, RestfulFile file) {

        try
        {
            FileStates nextState = FileStates.valueOf(file.getState());

            FileStates currentState = patientFile.getCurrentStatus().getState();

            //If the current state is currently inpatient and the upcoming state of the file is not in inpatient , that means
            if(currentState.isCurrentlyInPatient() && !nextState.isCurrentlyInPatient() && (nextState != FileStates.INPATIENT_COMPLETED))
            {
                patientFile.setProcessed(false);
            }else if (currentState.isCurrentlyInPatient() && !nextState.isCurrentlyInPatient() && (nextState == FileStates.INPATIENT_COMPLETED))
            {
                patientFile.setProcessed(true);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    private boolean addHistoryToExistingPatientFile(PatientFile patientFile, RestfulFile file, Employee emp, Appointment appointment) throws Exception {

        FileStates state = FileStates.valueOf(file.getState());

        switch (state)
        {
            case COORDINATOR_OUT:
            {
                //Add the current state as is
                this.addNewHistory(file,patientFile,emp,appointment);

                //check if that file has multiple appointments today
                List<Appointment> todayAppointments = getSystemService().getAppointmentManager().getTransfersFor(patientFile.getFileID(),patientFile.getCurrentStatus().getAppointment()
                .getAppointment_Date());

                if(todayAppointments != null && todayAppointments.size() > 0)
                {
                    //get the first appointment from the list
                    Appointment activeAppointment = todayAppointments.get(0);
                    //set the file state to transferred
                    file.setState(FileStates.TRANSFERRED.toString());
                    //then create a new file history with the new active appointment
                    this.addNewHistory(file,patientFile,emp,activeAppointment);
                    //set the new chosen appointment to inactive as well because it is being used already
                    activeAppointment.setActive(false);
                    getSystemService().getAppointmentManager().updateEntity(activeAppointment);

                }

            }
            break;

            case INPATIENT_COMPLETED:
            {
                patientFile.setProcessed(true);
                this.addNewHistory(file,patientFile,emp,appointment);

            }
            break;

            case CHECKED_IN:
            {
                //add the normal history to that file
                this.addNewHistory(file,patientFile,emp,appointment);

                //then check if there are some appointments remove them from the watch list

                List<Appointment> appointments = systemService.getAppointmentManager().searchWatchListAppointments(file.getFileNumber(),true);

                if(appointments != null && appointments.size() > 0)
                {
                    for(Appointment app : appointments)
                    {
                        app.setWatchList(false);

                        systemService.getAppointmentManager().updateEntity(app);
                    }
                }


            }
            break;

            case OUT_OF_CABIN:
            {
                this.addNewHistory(file,patientFile,emp,appointment);

                //then check if there are some appointments remove them from the watch list

                List<Appointment> appointments = systemService.getAppointmentManager().searchWatchListAppointments(file.getFileNumber(),false);

                if(appointments != null && appointments.size() > 0)
                {
                    for(Appointment app : appointments)
                    {
                        app.setWatchList(true);

                        systemService.getAppointmentManager().updateEntity(app);
                    }
                }

            }

            default:
            {
                this.addNewHistory(file,patientFile,emp,appointment);
            }
            break;
        }

        if(state.isCurrentlyInPatient())
            patientFile.setProcessed(file.isProcessed());


        //finally return success
        return true;
    }

    /**
     * This method will add a completely new File History to the file
     * @param file
     * @param newPatientFile
     * @param emp
     * @param appointment
     */
    private void addNewHistory(RestfulFile file, PatientFile newPatientFile, Employee emp, Appointment appointment) {

        try
        {
            FileHistory newFileHistory = new FileHistory();
            newFileHistory.setAppointment(appointment);
            newFileHistory.setContainerId(file.getTemporaryCabinetId());
            newFileHistory.setCreatedAt(new Date());
            newFileHistory.setOwner(emp);
            newFileHistory.setPatientFile(newPatientFile);
            FileStates state = FileStates.valueOf(file.getState());
            newFileHistory.setState(state);
            newPatientFile.setCurrentStatus(newFileHistory);

            if(state.isCurrentlyInPatient())
                newPatientFile.setProcessed(file.isProcessed());

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


//    @Override
//    public boolean updateFile(RestfulFile file, Employee emp)
//            throws RecordNotFoundException, WorkflowOutOfBoundException {
//
//        try
//        {
//            //First check to see if the request is found
//            Request foundRequest = getSystemService().getRequestsManager().getRequestByBatchNumber(
//                    file.getAppointmentId(), file.getBatchRequestNumber());
//
//
//            //Check the patient file
//            boolean patientFileExists = getSystemService().getFilesService().fileExists(file.getAppointmentId());
//
//            if(foundRequest != null && !patientFileExists)
//            {
//                //That means the current restful file is just checked out from the keeper
//                //Create a new Patient File
//                PatientFile newPatientFile = new PatientFile();
//                //create a new file cabinet
//                ArchiveCabinet cabinet = null;
//
//                cabinet = getSystemService().getCabinetsService().getCabinetByID(file.getCabinetId());
//
//                if(cabinet != null)
//                {
//                    newPatientFile.setArchiveCabinet(cabinet);
//
//                }else
//                {
//                    cabinet = new ArchiveCabinet();
//                    cabinet.setCabinetID(file.getCabinetId());
//                    cabinet.setCreationTime(new Date());
//
//                    //save the cabin into the database
//                    getSystemService().getCabinetsService().addEntity(cabinet);
//
//                    cabinet = getSystemService().getCabinetsService().getCabinetByID(cabinet.getCabinetID());
//
//                    newPatientFile.setArchiveCabinet(cabinet);
//                }
//
//                newPatientFile.setCreationTime(new Date());
//                newPatientFile.setFileID(file.getAppointmentId());
//                newPatientFile.setShelfId(file.getShelfId());
//                newPatientFile.setPatientName(foundRequest.getPatientName());
//                newPatientFile.setPatientNumber(foundRequest.getPatientNumber());
//                this.addNewFileHistory(newPatientFile, file, emp,foundRequest);
//
//                boolean result = getSystemService().getFilesService().addEntity(newPatientFile);
//
//                if (result) {
//                    //now remove the current request
//                    return getSystemService().getRequestsManager().removeEntity(foundRequest);
//
//                } else return false;
//
//            }else
//            {
//
//                /*if(foundRequest != null)
//                {
//                    getSystemService().getRequestsManager().removeEntity(foundRequest);
//                }*/
//                //that means the current request is not found
//                //it means it is not the first time for that file in the system
//                //Get the file by knowing its file number
//                PatientFile patientFile = getSystemService().getFilesService()
//                        .getFileWithNumber(file.getAppointmentId());
//
//
//
//                if(patientFile == null) throw new RecordNotFoundException("There is something wrong " +
//                        "in the system , Please contact your system administrator");
//
//                long serverTimeStamp = patientFile.getCurrentStatus().getCreatedAt().getTime();
//
//
//                if(file.getOperationDate() != null && foundRequest == null &&
//                        serverTimeStamp != file.getOperationDate()) return true;
//
//                //now update the current Patient File with the restful File
//                patientFile.updateWithRestful(file);
//
//                //then add new file History to that
//                if(foundRequest != null)
//                {
//                    this.addNewFileHistory(patientFile,file,emp,foundRequest);
//                    getSystemService().getRequestsManager().removeEntity(foundRequest);
//                }else
//                {
//                    this.addNewFileHistory(patientFile,file,emp);
//
//                }
//
//
//                //finally update the current patient file
//                return getSystemService().getFilesService().updateEntity(patientFile);
//
//
//            }
//
//        }catch (Exception s)
//        {
//            s.printStackTrace();
//            return false;
//        }
//    }



   /* private void addNewFileHistory(PatientFile patientFile,RestfulFile file , Employee emp , Request request)
            throws Exception
    {
        FileHistory history = new FileHistory();
        history.setContainerId(file.getTemporaryCabinetId());
        history.setOwner(emp);
        FileStates state = FileStates.valueOf(file.getState().toString());
        history.setState(state);
        //I have deactivated this feature because it allows for mixing different time periods
        //coming from the browser to the server which both might not be in sync, please look at
        //the YouTrack Task, for a bug related to improper display of Patient File's FileHistories
        *//*if(file.getOperationDate() == null)
            file.setOperationDate(new Date().getTime());*//*

        //Always set the time of operation to the server side Received Date to solve
        //the above problem
        file.setOperationDate(new Date().getTime());

        history.setCreatedAt(new Date());
        history.setPatientFile(patientFile);
        history.setInpatient(file.isInpatient());
        history.setAppointment_Hijri_Date(file.getAppointmentDateH());
        history.setAppointment_Made_by(file.getAppointmentMadeBy());
        history.setBatchRequestNumber(file.getBatchRequestNumber());
        history.setAppointmentType(file.getAppointmentType());
        history.setClinicCode(file.getClinicCode());
        history.setClinicDocCode(file.getClinicDocCode());
        history.setClinicDocName(file.getClinicDocName());
        history.setClinicName(file.getClinicName());
        history.setAppointment_Date_G(request.getAppointment_Date());

        patientFile.setCurrentStatus(history);
    }*/


    /*private void addNewFileHistory(PatientFile patientFile, RestfulFile file, Employee emp) {

        FileHistory history = new FileHistory();
        history.setContainerId(file.getTemporaryCabinetId());
        history.setOwner(emp);
        FileStates state = FileStates.valueOf(file.getState().toString());
        history.setState(state);
        //I have deactivated this feature because it allows for mixing different time periods
        //coming from the browser to the server which both might not be in sync, please look at
        //the YouTrack Task, for a bug related to improper display of Patient File's FileHistories
        *//*if(file.getOperationDate() == null)
            file.setOperationDate(new Date().getTime());*//*

        //Always set the time of operation to the server side Received Date to solve
        //the above problem

        if(file.getOperationDate() != -1 && file.getOperationDate() != null)
        {
            history.setCreatedAt(new Date(file.getDeviceOperationDate()));
        }else
        {
            history.setCreatedAt(new Date());
        }


        history.setPatientFile(patientFile);
        history.setInpatient(file.isInpatient());
        history.setAppointment_Hijri_Date(file.getAppointmentDateH());
        history.setAppointment_Made_by(file.getAppointmentMadeBy());
        history.setBatchRequestNumber(file.getBatchRequestNumber());
        history.setAppointmentType(file.getAppointmentType());
        history.setClinicCode(file.getClinicCode());
        history.setClinicDocCode(file.getClinicDocCode());
        history.setClinicDocName(file.getClinicDocName());
        history.setClinicName(file.getClinicName());
        try
        {
            if(file.getAppointmentDate() != null && file.getAppointmentDate().length() > 0)
            {
                SimpleDateFormat formatter = new SimpleDateFormat();

                Date appointmentDate = formatter.parse(file.getAppointmentDate());

                history.setAppointment_Date_G(appointmentDate);

            }else
            {
                history.setAppointment_Date_G(patientFile.getCurrentStatus().getAppointment_Date_G());
            }

        }catch (Exception s)
        {
            history.setAppointment_Date_G(patientFile.getCurrentStatus().getAppointment_Date_G());
        }

        patientFile.setCurrentStatus(history);
    }*/

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

            List<PatientFile> files = getSystemService().getFilesService().searchFiles(query);

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

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }
}
