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

                        if(hasTransfer &&  file.getState().equals(FileStates.COORDINATOR_OUT.toString())
                                &&
                                currentEmployee.getRole().getName().equals(RoleTypes.COORDINATOR.toString()))
                        {
                            //that means the syncing comes from a coordinator and the file has transfer
                            //so transfer that file
                            List<Transfer> transferList = controller.getSystemService().getTransferManager()
                                    .getTransfers(file.getFileNumber());

                            if(transferList != null && transferList.size() > 0)
                            {
                                controller.updateFile(file,currentEmployee);
                                //Sort them according to the appointment time
                                Collections.sort(transferList);
                                //get the first Transfer
                                Transfer tobeTransferredTo = transferList.get(0);

                               /* //Get the coordinator that has the current clinic assigned to him
                                List<Employee> coordinators = controller.getSystemService()
                                        .getEmployeeService().getEmployeesForClinicCode(tobeTransferredTo.getClinicCode());*/

                                    Employee owner = currentEmployee;

                                    FileHistory transferrableHistory = tobeTransferredTo.toFileHistory();
                                    transferrableHistory.setOwner(owner);

                                    PatientFile patientFile = controller.getSystemService().getFilesService().getFileWithNumber(file.getFileNumber());
                                    transferrableHistory.setPatientFile(patientFile);

                                    //now add that history to the current patient file and update it
                                    patientFile.setCurrentStatus(transferrableHistory);

                                    //now update that patient file
                                    boolean result = controller.getSystemService().getFilesService().updateEntity(patientFile);
                                    result &= controller.getSystemService().getTransferManager().removeEntity(tobeTransferredTo);

                                    if(!result)
                                        failedBatches.getFiles().add(file);





                            }else
                            {
                                failedBatches.getFiles().add(file);
                            }


                            //continue in the looping
                            continue;
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




}
