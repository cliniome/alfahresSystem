package com.degla.restful;

import com.degla.restful.models.BooleanResult;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by snouto on 20/05/15.
 */
@Path("/health")
@Scope("prototype")
public class HealthService {



    @GET
    @Path("/")
    @Produces("application/json")
    public Response health()
    {
        return Response.ok(new BooleanResult(true,"Server is running.")).build();
    }


}
