package com.degla.restful;
import com.degla.controllers.BasicController;
import com.degla.db.models.Employee;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.db.models.RoleTypes;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.*;
import com.degla.restful.utils.EmployeeUtils;
import com.degla.restful.utils.RestGsonBuilder;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.EmployeeLazyModel;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.misc.BASE64Encoder;
import static javax.ws.rs.core.Response.Status.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by snouto on 17/05/15.
 */
// The Java class will be hosted at the URI path "/helloworld"
@Path("/files")
public class FilesService extends BasicRestful {

    private SystemService systemService;

    public FilesService()
    {
        try
        {
            systemService = SpringSystemBridge.services();

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }

    @Path("/distribute")
    @Produces("application/json")
    @GET
    public Response distributedFiles()
    {

        try
        {
            Employee emp = getAccount();

            if(emp == null)
                return Response.status(UNAUTHORIZED).build();
            else
            {
                if(systemService == null)
                    systemService = SpringSystemBridge.services();

                List<PatientFile> files = systemService.getFilesService().getFilesByStateAndEmployee(
                        FileStates.COORDINATOR_IN,emp);

                if(files == null) files = new ArrayList<PatientFile>();

                List<RestfulFile> restFiles = new ArrayList<RestfulFile>();

                for(PatientFile file : files)
                {
                    restFiles.add(file.toRestfulFile());
                }

                //now create a sync Batch
                SyncBatch batch = new SyncBatch(restFiles);
                batch.setCreatedAt(new Date().getTime());
                batch.setState(true);

                //now return the sync Batch
                return Response.ok(batch).build();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("/scan")
    @GET
    @Produces("application/json")
    public Response scanFiles(@QueryParam("query") String query)
    {
        try
        {
            Employee emp = getAccount();

            if(emp == null)
                return Response.status(UNAUTHORIZED).build();

            BasicController controller = new BasicController();

            List<RestfulFile> files = controller.scanFiles(query,EmployeeUtils.getScannableStates(emp));

            if(files == null || files.size() <=0) throw new Exception("There are no Files for the Moment.");

            SyncBatch batch = new SyncBatch(files);
            batch.setCreatedAt(new Date().getTime());
            batch.setState(true);
            batch.setMessage("Batch is loaded with files");

            return Response.ok(batch).build();

        }catch (Exception s)
        {
            return Response.ok(new BooleanResult(false,s.getMessage())).build();
        }
    }


    @Path("/search/{query}")
    @GET
    @Produces("application/json")
    public Response searchForFiles(@PathParam("query") String query)
    {
        BasicController controller = new BasicController();

        List<RestfulFile> foundFiles = controller.searchFiles(query);

        if(foundFiles == null || foundFiles.size() <=0)
            return Response.noContent().build();
        else return Response.ok(foundFiles).build();

    }


    @Path("/single")
    @POST
    @Produces("application/json")
    @Consumes("*/*")
    public Response updateFile(RestfulFile file)
    {
        BasicController controller = new BasicController();
        Employee emp = getAccount();
        Gson gson = RestGsonBuilder.createGson();

        if(emp == null)
            return Response.status(UNAUTHORIZED).build();
        else
        {
            try
            {
                boolean result = controller.updateFile(file, emp);

                if(result)
                {
                    return Response.ok(gson.toJson(new BooleanResult(result,"Patient File has been Updated Successfully")))
                            .build();

                }else
                    return Response.ok(new BooleanResult(false,"There was a problem processing the current file")).build();



            }catch (RecordNotFoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();

            }catch(WorkflowOutOfBoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }catch(EntityNotFoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }

        }
    }


    @Path("/collect")
    @POST
    @Produces("application/json")
    public Response collectFiles()
    {
        try
        {
            //get the current Employee account
            Employee currentEmp = getAccount();

            if(currentEmp == null ||
                    !currentEmp.getRole().getName()
                            .equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))

                return Response.status(UNAUTHORIZED).build();

            if(systemService == null)
                systemService = SpringSystemBridge.services();

            List<PatientFile> availableFiles = systemService.getFilesService().collectFiles(currentEmp);

            if(availableFiles != null && availableFiles.size() > 0)
            {
                CollectionBatch batch  = new CollectionBatch();

                List<RestfulClinic> clinics = new ArrayList<RestfulClinic>();

                for(PatientFile file : availableFiles)
                {
                    RestfulClinic currentClinic = getRestfulClinicByCode(file.getCurrentStatus().getClinicCode(),clinics);

                    if(currentClinic == null)
                    {
                        RestfulClinic clinic = new RestfulClinic();
                        clinic.setClinicCode(file.getCurrentStatus().getClinicCode());
                        clinic.setClinicName(file.getCurrentStatus().getClinicName());
                        RestfulFile restfile = file.toRestfulFile();

                        boolean hasMultipleClinics = systemService.getTransferManager().hasTransfer(file.getFileID());
                        restfile.setHasMultipleClinics(hasMultipleClinics);
                        clinic.getFiles().add(restfile);

                        //now add it to the current clinics list
                        clinics.add(clinic);
                    }else
                    {
                        //add the current file to the current clinic
                        currentClinic.getFiles().add(file.toRestfulFile());
                    }
                }

                //once , this is done , now add it to the collection batch
                batch.setCreatedAt(new Date().getTime());
                batch.setClinics(clinics);

                //now return the response
                return Response.ok(batch).build();


            }else
            {
                return  Response.ok(new BooleanResult(false,"There are no Files to collect For the Moment.")).build();
            }


        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(UNAUTHORIZED).build();
        }
    }

    private RestfulClinic getRestfulClinicByCode(String clinicCode,List<RestfulClinic> clinics)
    {
        RestfulClinic clinic = null;

        for(RestfulClinic current : clinics)
        {
            if(current.getClinicCode().equalsIgnoreCase(clinicCode))
            {
                clinic = current;
                break;
            }
        }

        return clinic;


    }


    @Path("/multiple")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateFiles(List<RestfulFile> files)
    {
        BasicController controller = new BasicController();
        Employee emp = getAccount();
        Gson gson = RestGsonBuilder.createGson();

        if(emp == null) return Response.status(UNAUTHORIZED).build();
        else
        {
            try
            {
                boolean result = controller.updateFiles(files, emp);

                if(result) return Response.ok(gson.toJson(new BooleanResult(result,"Patient Files Have Been Updated Successfully")))
                        .build();
                else
                    return Response.ok(new BooleanResult(false,"There was a problem processing the current Request")).build();

            }catch (RecordNotFoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }catch(WorkflowOutOfBoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }
        }
    }


   /* private Employee getAccount()
    {
       try
       {
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

           if(authentication.isAuthenticated() && authentication.getPrincipal() != null
                   && authentication.getPrincipal() instanceof Employee)
           {
               Employee emp = (Employee)authentication.getPrincipal();

               return emp;

           }else return null;

       }catch (Exception s)
       {
           return null;
       }
    }*/

    @Path("/new")
    @GET
    @Produces("application/json")
   public Response newRequests()
   {
       BasicController controller = new BasicController();

       Employee emp = getAccount();

        if(emp != null)
        {
            String currentUserName = emp.getUserName();

            Gson gson = RestGsonBuilder.createGson();

            List<RestfulRequest> availableRequests = controller.getNewRequests(currentUserName);

            if(availableRequests == null)

                return Response.noContent().build();

            else return Response.ok(gson.toJson(availableRequests)).build();

        }else
            return Response.status(UNAUTHORIZED).build();
   }



}
