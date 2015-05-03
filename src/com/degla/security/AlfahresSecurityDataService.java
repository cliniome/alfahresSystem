package com.degla.security;
import com.degla.api.Authenticator;
import com.degla.beans.LoginBean;
import com.degla.db.models.Employee;
import com.degla.system.SystemService;
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

    @Autowired
    protected SystemService alfahresService;


    public void setApplicationContext(ApplicationContext con)
            throws BeansException {

        this.context = con;


    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        try
        {
            //Check the current user from the active integration
            LoginBean loginBean = (LoginBean)JSFUtils.getAnyBeanByName("loginBean", LoginBean.class);


            //Check user access into the system
            if(checkAccess(username, loginBean.getPassword()))
            {
                //if the current user is authenticated
                //allow access to the system by grabbing the current user from the internal system
                Employee currentEmployee = alfahresService.getEmployeeService().getEmployeeByUserName(username);

                currentEmployee.setPassword(loginBean.getPassword());

                return currentEmployee;


            }else return null;

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    private boolean checkAccess(String username,String password)
    {
        try
        {
            //Now check User Access
            //Get the active Connector
            Authenticator authenticator = alfahresService.getAuthenticatorService();

            return authenticator.authenticate(username,password);


        }catch(Exception s)
        {
           s.printStackTrace();
            return false;
        }
    }


}
