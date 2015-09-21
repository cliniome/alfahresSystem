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

                    //If the file comes as sent out by coordinator
                    boolean updateResult = controller.updateFile(file,currentEmployee);

                    if(!updateResult)
                    {
                        //if the update process status is failed , so that means updating that file has failed ,
                        //add it to the failed requests batch and then continue
                        failedBatches.getFiles().add(file);
                        continue;
                    }

                    //That means the current file has been updated successfully , so check for transfers
                   // this.checkFileForTransfer(file,currentEmployee,controller,failedBatches);
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
