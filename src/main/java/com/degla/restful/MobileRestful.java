package com.degla.restful;
import com.degla.controllers.BasicController;
import com.degla.db.models.Employee;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by snouto on 17/05/15.
 */
// The Java class will be hosted at the URI path "/helloworld"
@Path("/")
public class MobileRestful {

    private SystemService systemService;

    public MobileRestful()
    {
        try
        {
            systemService = SpringSystemBridge.services();

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }


    @Path("files/single")
    @POST
    @Produces("application/json")
    public Response updateFile(@QueryParam("file")RestfulFile file)
    {
        BasicController controller = new BasicController();
        Employee emp = getAccount();

        if(emp == null)
            return Response.status(UNAUTHORIZED).build();
        else
        {
            boolean result = controller.updateFile(file,emp);

            if(result)
            {
                return Response.ok(new BooleanResult(result,"Patient File has been Updated Successfully")).build();
            }else
                return Response.status(NOT_ACCEPTABLE).build();

        }
    }


    @Path("files/multiple")
    @POST
    @Produces("application/json")
    public Response updateFiles(@QueryParam("files") List<RestfulFile> files)
    {
        BasicController controller = new BasicController();
        Employee emp = getAccount();

        if(emp == null) return Response.status(UNAUTHORIZED).build();
        else
        {
            boolean result = controller.updateFiles(files,emp);

            if(result) return Response.ok(new BooleanResult(result,"Patient Files Have Been Updated Successfully"))
            .build();
            else
                return Response.status(NOT_ACCEPTABLE).build();
        }
    }




    private Employee getAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.isAuthenticated() && authentication.getPrincipal() != null)
        {
            Employee emp = (Employee)authentication.getDetails();

            return emp;

        }else return null;
    }

    @Path("requests")
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

        }else return Response.status(UNAUTHORIZED).build();
   }



}
