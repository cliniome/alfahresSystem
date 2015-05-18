package com.degla.restful.security;

import com.mysql.jdbc.util.Base64Decoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by snouto on 18/05/2015.
 */
public class RestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    @Autowired
    private AuthenticationManager authenticationManager;



    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try
        {
            //extract the authentication header from the request
            String AuthenticationString = request.getHeader("Authorization");
            //now decode the authentication Header
            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(AuthenticationString);
            //Convert these decoded bytes array into String utf-8
            String decodedCredentials = new String(decodedBytes,"UTF-8");

            boolean retVal = false;

            //split it at the occurrences of ":"
            String[] splitted = decodedCredentials.split(":");

            if(splitted == null || splitted.length <= 1) return false;

            String username= splitted[0];
            String password = splitted[1];

            if (username != null && password != null) {
                Authentication authResult = null;
                try {
                    authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
                    if (authResult == null) {
                        retVal = false;
                    }
                } catch (AuthenticationException failed) {
                    try {
                        unsuccessfulAuthentication(request,response,failed);
                    } catch (IOException e) {
                        retVal = false;
                    } catch (ServletException e) {
                        retVal = false;
                    }
                    return (retVal = false);
                }
                try {
                    successfulAuthentication(request, response, authResult);
                } catch (IOException e) {
                    retVal = false;
                } catch (ServletException e) {
                    retVal = false;
                }
                return false;
            } else {
                retVal = true;
            }
            return retVal;


        }catch(Exception s)
        {
            s.printStackTrace();
             try
             {
                 unsuccessfulAuthentication(request,response,null);

             }catch(Exception e)
             {

             }
            return true;
        }

    }
}
