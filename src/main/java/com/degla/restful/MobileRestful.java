package com.degla.restful;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.sun.net.httpserver.HttpServer;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

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

    //Login method
    @GET
    @Produces("text/plain")
    public String helloWorld()
    {
        return "Hello World";
    }

}
