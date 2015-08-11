package com.degla.restful;

import com.degla.controllers.EmployeeController;
import com.degla.db.models.Employee;
import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulEmployee;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by snouto on 20/05/15.
 */
@Path("/employee")
@Scope("prototype")
public class EmployeeService extends BasicRestful {


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


    @GET
    @Path("/login")
    @Produces("application/json")
    @Consumes("application/json")
    public Response login()
    {
        try
        {

            Employee emp = getAccount();

            if(emp == null) throw new Exception("Unauthorized");

            RestfulEmployee restEmp = new RestfulEmployee();
            restEmp.setFirstName(emp.getFirstName());
            restEmp.setId(emp.getId());
            restEmp.setLastName(emp.getLastName());
            restEmp.setPassword(emp.getPassword());
            restEmp.setUserName(emp.getUserName());
            restEmp.setRole(emp.getRole().getName());


            return Response.ok(restEmp).build();

        }catch (Exception s)
        {
            s.printStackTrace();

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }



}
