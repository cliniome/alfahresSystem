package com.degla.restful;

import com.degla.controllers.BasicController;
import com.degla.db.models.Employee;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.Serializable;

/**
 * Created by snouto on 22/05/15.
 */
@Path("/sync")
public class SyncService implements Serializable {


    @Path("/now")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response sync(SyncBatch batch)
    {
        SyncBatch failedBatches = new SyncBatch();
        failedBatches.setMessage("Files failed to be synchronized");
        failedBatches.setState(false);

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

    private Employee getAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.isAuthenticated() && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof Employee)
        {
            Employee emp = (Employee)authentication.getPrincipal();

            return emp;

        }else return null;
    }


}
