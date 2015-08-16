package com.degla.restful;

import com.degla.controllers.BasicController;
import com.degla.db.models.*;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.utils.AlfahresDateUtils;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.FileRouter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Scope;
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
@Scope("prototype")
public class SyncService extends BasicRestful implements Serializable {


    @Path("/now")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public synchronized Response sync(SyncBatch batch)
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

                //Enumerate on all the files to update them accordingly

                for(RestfulFile file : batch.getFiles())
                {
                    boolean updateResult = controller.updateFile(file,currentEmployee);

                    if(!updateResult)
                    {
                        //if the update process status is failed , so that means updating that file has failed ,
                        //add it to the failed requests batch and then continue
                        failedBatches.getFiles().add(file);
                        continue;
                    }

                    //That means the current file has been updated successfully , so check for transfers
                    this.checkFileForTransfer(file,currentEmployee,controller,failedBatches);
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

    private void checkFileForTransfer(RestfulFile file, Employee currentEmployee,BasicController controller,SyncBatch failedBatches) {

        try
        {
            boolean hasTransfer = controller.getSystemService().getTransferManager().hasTransfer(file.getFileNumber());
            List<Transfer> transferList = controller.getSystemService().getTransferManager()
                    .getTransfers(file.getFileNumber());

            PatientFile patientFile = controller.getSystemService().getFilesService().getFileWithNumber(file.getFileNumber());

            //Sort them according to the appointment time
            Collections.sort(transferList);

            if(hasTransfer && transferList != null && transferList.size() > 0)
            {

                Transfer recentTransfer = transferList.get(0);

                if(patientFile.getCurrentStatus().getState().equals(FileStates.COORDINATOR_OUT.toString()))
                {
                    //that means the file has a transfer and the coordinator is submitting that file
                    //So check for the dates of the current transfer with the patient file
                    //if the dates are in the same day , that means it is a transfer ,
                    //otherwise , it is not a transfer
                    //that means it is a true transfer , so transfer that file
                    FileHistory transferrableHistory = recentTransfer.toFileHistory();
                    transferrableHistory.setOwner(currentEmployee);


                    transferrableHistory.setPatientFile(patientFile);

                    //now add that history to the current patient file and update it
                    patientFile.setCurrentStatus(transferrableHistory);

                    //now update that patient file
                    boolean result = controller.getSystemService().getFilesService().updateEntity(patientFile);
                    result &= controller.getSystemService().getTransferManager().removeEntity(recentTransfer);

                    if(!result)
                        failedBatches.getFiles().add(file);


                    //Otherwise , if that file  has been submitted by keeper during the final process of the lifecycle at the archiving step
                }else if (patientFile.getCurrentStatus().getState().equals(FileStates.CHECKED_IN.toString()))
                {
                    transferList = controller.getSystemService().getTransferManager().getFutureTransfer(file.getFileNumber());

                    if(transferList != null && transferList.size() > 0)
                    {
                        Transfer futureTransfer = transferList.get(0);

                        //That means it is properly a new request, so add it
                        Request transferRequest = futureTransfer.toRequestObject();

                        //Route the current request
                        List<Request> tempRequests = new ArrayList<Request>();
                        tempRequests.add(transferRequest);

                        FileRouter router = new FileRouter();
                        router.routeFiles(tempRequests);

                        //after that , try to add the current request into the database
                        boolean result = controller.getSystemService().getRequestsManager().addEntity(transferRequest);
                        result &= controller.getSystemService().getTransferManager().removeEntity(recentTransfer);
                    }

                }

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }


    private boolean transferInTheSameDay(Date date, Date appointment_date_g) {

        return DateUtils.isSameDay(date,appointment_date_g);
    }


}
