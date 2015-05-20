package com.degla.restful;

import com.degla.controllers.EmployeeController;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulEmployee;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by snouto on 20/05/15.
 */
@Path("/employee")
public class EmployeeService {


    public EmployeeService(){


    }

    @POST
    @Path("/profile")
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateEmployee(RestfulEmployee emp)
    {
        if(emp == null)
            return Response.ok(new BooleanResult(false,"No information has been provided to update.")).build();

        EmployeeController controller = new EmployeeController();

        boolean result = controller.updateProfile(emp);

        if(result)
            return Response.ok(new BooleanResult(true,"Your Profile has been updated.")).build();
        else return Response.ok(new BooleanResult(false,"There was a problem , Please contact System Administrator"))
        .build();
    }



}
