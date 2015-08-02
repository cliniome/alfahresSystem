package com.degla.restful;
import com.degla.controllers.BasicController;
import com.degla.db.models.*;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.exceptions.WorkflowOutOfBoundException;
import com.degla.restful.models.*;
import com.degla.restful.utils.EmployeeUtils;
import com.degla.restful.utils.RestGsonBuilder;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.BarcodeUtils;
import com.degla.utils.EmployeeLazyModel;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.misc.BASE64Encoder;
import static javax.ws.rs.core.Response.Status.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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


    @Path("/transferInfo")
    @GET
    @Produces("application/json")
    public Response getTransferInfo(@QueryParam("fileNumber") String fileNumber)
    {
        try
        {
            Employee emp = getAccount();

            if(emp == null)
                return Response.status(UNAUTHORIZED).build();


            List<Transfer> transfers = systemService.getTransferManager().getTransfers(fileNumber);

            if(transfers == null || transfers.size() <= 0 )
                return Response.status(NOT_FOUND).build();

            //sort them according to the appointment time
            Collections.sort(transfers);

            //get the first one
            Transfer currentTransfer = transfers.get(0);

            //Create a transfer info card
            RestfulTransferInfo info = new RestfulTransferInfo();
            info.setAppointmentDate(currentTransfer.getAppointment_Hijri_Date());
            info.setAppointmentTime(currentTransfer.getAppointmentTime());
            info.setClinicCode(currentTransfer.getClinicCode());
            info.setClinicName(currentTransfer.getClinicName());
            info.setClinicDocCode(currentTransfer.getClinicDocCode());
            info.setClinicDocName(currentTransfer.getClinicDocName());
            info.setInpatient(currentTransfer.isInpatient());

            String coordinatorName = "<No Coordinator Available>";

            List<Employee> employeeList = systemService.getEmployeeService()
                    .getEmployeesForClinicCode(currentTransfer.getClinicCode());

            if(employeeList != null && employeeList.size() > 0)
            {
                //take the first coordinator in this list
                Employee coordinator = employeeList.get(0);
                coordinatorName = coordinator.getfullName();
            }

            info.setCoordinatorName(coordinatorName);

            return Response.ok(info).build();




        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(NOT_FOUND).build();
        }
    }


    @Path("/scanOneFile")
    @GET
    @Produces("application/json")
    public Response ScanOneFile(@QueryParam("fileNumber") String fileNumber)
    {
        try
        {
            //get the employee
            Employee emp = getAccount();

            if(emp == null)
            {
                return Response.status(UNAUTHORIZED).build();
            }else
            {
                SyncBatch batch = new SyncBatch();
                batch.setCreatedAt(new Date().getTime());

                List<RestfulFile> foundFiles = new ArrayList<RestfulFile>();
                //get the file
                PatientFile foundFile = systemService.getFilesService().getSpecificFileNumberForCoordinator
                        (fileNumber, emp);

                if(foundFile != null)
                {
                    foundFiles.add(foundFile.toRestfulFile());
                }

                batch.setFiles(foundFiles);

                return Response.ok(batch).build();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(NOT_FOUND).build();
        }
    }

    @Path("/sortFile")
    @GET
    @Produces("application/json")
    public Response getSortFiles(@QueryParam("fileNumber") String fileNumber)
    {
        try
        {
            //get the employee
            Employee emp = getAccount();

            if(emp == null || !(emp.getRole().getName().equals(RoleTypes.KEEPER.toString())))
            {
                return Response.status(UNAUTHORIZED).build();

            }else
            {
                SyncBatch batch = new SyncBatch();
                batch.setCreatedAt(new Date().getTime());

                List<RestfulFile> foundFiles = new ArrayList<RestfulFile>();
                //get the file
                PatientFile foundFile = systemService.getFilesService().
                        getFileWithNumberAndState(fileNumber, FileStates.OUT_OF_CABIN);


                if(foundFile == null)
                {
                    return Response.ok(new BooleanResult(false,"This file might not exist or it is not " +
                            "prepared by keeper yet")).build();
                }

                if(foundFile != null)
                {
                    foundFiles.add(foundFile.toRestfulFile());
                }

                batch.setFiles(foundFiles);
                batch.setState(true);

                return Response.ok(batch).build();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/fileDetails")
    @GET
    @Produces("application/json")
    public Response fileDetails(@QueryParam("fileNumber") String fileNumber)
    {
        try
        {
            //get the employee
            Employee emp = getAccount();

            if(emp == null)
            {
                return Response.status(UNAUTHORIZED).build();
            }else
            {
                FileBatchDetails batch = null;

                //get the file
                PatientFile foundFile = systemService.getFilesService().getFileWithNumber(fileNumber);

                if(foundFile != null)
                {
                    batch = new FileBatchDetails(foundFile.toRestfulFile(),foundFile.getCurrentStatus().getOwner().getfullName());
                }

                if(batch == null) throw new Exception("File not found");

                return Response.ok(batch).build();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @Path("/oneFile")
    @GET
    @Produces("application/json")
    public Response scanFile(@QueryParam("fileNumber") String fileNumber)
    {
        try
        {
            //get the employee
            Employee emp = getAccount();

            if(emp == null)
            {
                return Response.status(UNAUTHORIZED).build();
            }else
            {
                SyncBatch batch = new SyncBatch();
                batch.setCreatedAt(new Date().getTime());

                List<RestfulFile> foundFiles = new ArrayList<RestfulFile>();
                //get the file
                List<PatientFile> files = systemService.getFilesService().scanForIndividualFiles(fileNumber,
                        EmployeeUtils.getScannableStates(emp));

                if(files == null || files.isEmpty())
                    throw new Exception("Files not Found");

                for(PatientFile file : files)
                {
                    foundFiles.add(file.toRestfulFile());
                }


                batch.setFiles(foundFiles);

                return Response.ok(batch).build();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
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

            //Check for the passed in query
            BarcodeUtils utils = new BarcodeUtils(query);

            List<RestfulFile> files = null;

            String message = "There are no Files for the Moment.";

            if(utils.isNewFileStructure())
            {
                //That means the current file is individual file
                files = controller.scanIndividualFiles(query,EmployeeUtils.getScannableStates(emp));
            }else
            {
                //that means it is a trolley Barcode
                if((emp.getRole().getName().equals(RoleTypes.COORDINATOR.toString())) ||
                        emp.getRole().getName().equals(RoleTypes.RECEPTIONIST.toString()))
                {
                    files = controller.scanFiles(query,EmployeeUtils.getScannableStates(emp));

                }else
                {
                    files = new ArrayList<RestfulFile>();
                    message ="You can't Scan a trolley Barcode , You don't have permission to do that";
                }

            }

            if(files == null || files.size() <=0) throw new Exception(message);

            SyncBatch batch = new SyncBatch(files);
            batch.setCreatedAt(new Date().getTime());
            batch.setState(true);
            batch.setMessage("Batch is loaded with files");

            return Response.ok(batch).build();

        }catch (Exception s)
        {
            return Response.ok(new BooleanResult(false, s.getMessage())).build();
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


    /**
     * This function will be called by coordinator to collect files in his expandable ListView
     * @return
     */
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

                    boolean hasMultipleClinics = systemService.getTransferManager().hasTransfer(file.getFileID());
                    RestfulClinic currentClinic = getRestfulClinicByCode(file.getCurrentStatus().getClinicCode(),clinics);

                    if(currentClinic == null)
                    {
                        RestfulClinic clinic = new RestfulClinic();
                        clinic.setClinicCode(file.getCurrentStatus().getClinicCode());
                        clinic.setClinicName(file.getCurrentStatus().getClinicName());
                        RestfulFile restfile = file.toRestfulFile();
                        restfile.setMultipleClinics(hasMultipleClinics);
                        clinic.getFiles().add(restfile);
                        //now add it to the current clinics list
                        clinics.add(clinic);
                    }else
                    {
                        RestfulFile currenteFile = file.toRestfulFile();
                        currenteFile.setMultipleClinics(hasMultipleClinics);
                        //add the current file to the current clinic
                        currentClinic.getFiles().add(currenteFile);
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
