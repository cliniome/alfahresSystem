package com.degla.security;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by snouto on 02/05/2015.
 */
public class AlfahresSecurityDataService implements UserDetailsService, ApplicationContextAware {


    protected ApplicationContext context;

   /* @Autowired
    protected SystemService rvuService;*/


    public void setApplicationContext(ApplicationContext con)
            throws BeansException {

        this.context = con;


    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        try
        {
           /* //Check the current user from the active integration
            LoginBean loginBean = (LoginBean)JSFUtils.getAnyBeanByName("loginBean", LoginBean.class);


            //Check user access into the system
            if(checkAccess(username, loginBean.getPassword()))
            {
                //if the current user is authenticated
                //allow access to the system by grabbing the current user from the internal system
                PhysicianEO currentPhysician = rvuService.getPhysicianManager().getPhysicianByUserName(username);

                currentPhysician.setPassword(loginBean.getPassword());

                return currentPhysician;

            }else return null;*/

            return null;



        }catch(Exception s)
        {
           /* RVULogger.logMessage(s.getMessage());*/
            return null;
        }
    }

    private boolean checkAccess(String username,String password)
    {
        try
        {
           /* //Now check User Access
            //Get the active Connector
            APIConnector connector = rvuService.getIntegrationManager().getConnector();


            return connector.checkAccess(username, password, new Object[]{null} );*/

            return false;

        }catch(Exception s)
        {
           /* RVULogger.logMessage(s.getMessage());*/
            return false;
        }
    }


}
