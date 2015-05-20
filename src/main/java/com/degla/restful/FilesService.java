package com.degla.restful;
import com.degla.controllers.BasicController;
import com.degla.db.models.Employee;
import com.degla.exceptions.RecordNotFoundException;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.RestfulRequest;
import com.degla.restful.utils.RestGsonBuilder;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.misc.BASE64Encoder;
import static javax.ws.rs.core.Response.Status.*;
import java.io.IOException;
import java.util.List;
import javax.persistence.Basic;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by snouto on 17/05/15.
 */
// The Java class will be hosted at the URI path "/helloworld"
@Path("/files")
public class FilesService {

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


    @Path("/search/{query}")
    @GET
    @Produces("application/json")
    public Response searchForFiles(@PathParam("query") String query)
    {
        BasicController controller = new BasicController();

        List<RestfulFile> foundFiles = controller.searchFiles(query);

        if(foundFiles == null || foundFiles.size() <=0)
            return Response.ok(new BooleanResult(false,"No Available Files")).build();
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
                boolean result = controller.updateFile(file,emp);

                if(result)
                {
                    return Response.ok(gson.toJson(new BooleanResult(result,"Patient File has been Updated Successfully")))
                            .build();

                }else
                    return Response.ok(new BooleanResult(false,"There was a problem processing the current file")).build();



            }catch (RecordNotFoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }

        }
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
                boolean result = controller.updateFiles(files,emp);

                if(result) return Response.ok(gson.toJson(new BooleanResult(result,"Patient Files Have Been Updated Successfully")))
                        .build();
                else
                    return Response.ok(new BooleanResult(false,"There was a problem processing the current Request")).build();

            }catch (RecordNotFoundException e)
            {
                return Response.ok(new BooleanResult(false,e.getMessage())).build();
            }
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

                return Response.ok(new BooleanResult(true,"There are no Available Requests")).build();

            else return Response.ok(gson.toJson(availableRequests)).build();

        }else
            return Response.ok(new BooleanResult(false,"There was a problem , Try to login again")).build();
   }



}
