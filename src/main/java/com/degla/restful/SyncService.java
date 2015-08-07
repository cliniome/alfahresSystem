package com.degla.restful;

import com.degla.controllers.BasicController;
import com.degla.db.models.*;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileRouter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Basic;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
@Path("/sync")
public class SyncService extends BasicRestful implements Serializable {


    @Path("/now")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response sync(SyncBatch batch)
    {
        SyncBatch failedBatches = new SyncBatch();
        failedBatches.setMessage("Files failed to be synchronized");
        failedBatches.setState(false);
        failedBatches.setFiles(new ArrayList<RestfulFile>());

        try
        {

            BasicController controller = new BasicController();

            if(batch != null && batch.loaded())
            {
                Employee currentEmployee = getAccount();
                if(currentEmployee == null)
                    return Response.status(Response.Status.UNAUTHORIZED).build();

                for(RestfulFile file : batch.getFiles())
                {
                    try
                    {
                        boolean hasTransfer = controller.getSystemService().getTransferManager().hasTransfer(file.getFileNumber());

                        //that means the syncing comes from a coordinator and the file has transfer
                        //so transfer that file
                        List<Transfer> transferList = controller.getSystemService().getTransferManager()
                                .getTransfers(file.getFileNumber());

                        PatientFile patientFile = controller.getSystemService().getFilesService().getFileWithNumber(file.getFileNumber());

                        //Sort them according to the appointment time
                        Collections.sort(transferList);
                        //get the first Transfer

                        Employee owner = currentEmployee;

                        if(hasTransfer &&  file.getState().equals(FileStates.COORDINATOR_OUT.toString())
                                &&
                                currentEmployee.getRole().getName().equals(RoleTypes.COORDINATOR.toString()))
                        {

                            if(transferList != null && transferList.size() > 0)
                            {
                                controller.updateFile(file, currentEmployee);

                                Transfer tobeTransferredTo = transferList.get(0);

                                //Check if the current transfer is in the same day
                                if(this.transferInTheSameDay(new Date(),tobeTransferredTo.getAppointment_Date_G()))
                                {
                                    FileHistory transferrableHistory = tobeTransferredTo.toFileHistory();
                                    transferrableHistory.setOwner(owner);


                                    transferrableHistory.setPatientFile(patientFile);

                                    //now add that history to the current patient file and update it
                                    patientFile.setCurrentStatus(transferrableHistory);

                                    //now update that patient file
                                    boolean result = controller.getSystemService().getFilesService().updateEntity(patientFile);
                                    result &= controller.getSystemService().getTransferManager().removeEntity(tobeTransferredTo);

                                    if(!result)
                                        failedBatches.getFiles().add(file);

                                }

                               /* //Get the coordinator that has the current clinic assigned to him
                                List<Employee> coordinators = controller.getSystemService()
                                        .getEmployeeService().getEmployeesForClinicCode(tobeTransferredTo.getClinicCode());*/


                            }else
                            {
                                failedBatches.getFiles().add(file);
                            }


                            //continue in the looping
                            continue;
                        }else if(hasTransfer && file.getState().equals(FileStates.CHECKED_IN.toString()))
                        {
                            //first update the current file
                            controller.updateFile(file, currentEmployee);

                            //The current Transfer
                            Transfer tobeTransferredTo = transferList.get(0);

                            if(!this.transferInTheSameDay(new Date(),tobeTransferredTo.getAppointment_Date_G()))
                            {
                                //That means it is properly a new request, so add it
                                Request transferRequest = tobeTransferredTo.toRequestObject();

                                //Route the current request
                                List<Request> tempRequests = new ArrayList<Request>();
                                tempRequests.add(transferRequest);

                                FileRouter router = new FileRouter();
                                router.routeFiles(tempRequests);

                                //after that , try to add the current request into the database
                                boolean result = controller.getSystemService().getRequestsManager().addEntity(transferRequest);
                                result &= controller.getSystemService().getTransferManager().removeEntity(tobeTransferredTo);

                                if(!result)
                                    failedBatches.getFiles().add(file);
                            }



                        }

                        boolean result = controller.updateFile(file,currentEmployee);

                        if(!result)
                            failedBatches.getFiles().add(file);

                    }catch (RecordNotFoundException e)
                    {
                        failedBatches.getFiles().add(file);
                    }catch (WorkflowOutOfBoundException e)
                    {
                        failedBatches.getFiles().add(file);
                    }
                }

                if(failedBatches.loaded())
                    return Response.ok(failedBatches).build();
                else return Response.ok(new BooleanResult(true,"All Files have been Synchronized Successfully"))
                        .build();


            }else throw new Exception("Failed to Sync an empty batch");


        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.ok(new BooleanResult(false,s.getMessage())).build();
        }
    }

    private boolean transferInTheSameDay(Date date, Date appointment_date_g) {

        return DateUtils.isSameDay(date,appointment_date_g);
    }


}
