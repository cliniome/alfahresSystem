package com.degla.security;

import com.alfahres.beans.DashboardBean;
import com.degla.db.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

/**
 * Created by snouto on 02/05/2015.
 */
@Controller("loginService")
public class LoginService {

    @Autowired
    AuthenticationManager authenticationManager;



    public boolean dologOut()
    {
        try
        {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            SecurityContextHolder.getContext().setAuthentication(null);

            SecurityContextHolder.clearContext();

            return true;

        }catch(Exception s)
        {



            return false;
        }
    }

    public boolean dologin(String username , String password)
    {
        try
        {

            Authentication currentAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));


            if(currentAuthentication == null) return false;

            if(currentAuthentication.isAuthenticated())
            {
                DashboardBean dashboard = (DashboardBean)JSFUtils.getAnyBeanByName("dashboardBean", DashboardBean.class);
                dashboard.setAccount((Employee) currentAuthentication.getPrincipal());
                //now add it to the spring security
                //to let it memorize the current logged in user
                SecurityContextHolder.getContext().setAuthentication(currentAuthentication);
                return true;

            }

            return false;


        }catch(Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }
}
