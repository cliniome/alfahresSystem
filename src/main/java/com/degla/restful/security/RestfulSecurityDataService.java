package com.degla.restful.security;

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
 * Created by snouto on 18/05/2015.
 */
public class RestfulSecurityDataService implements UserDetailsService , ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    protected SystemService alfahresService;



    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Employee emp = alfahresService.getEmployeeService().getEmployeeByUserName(s);




        return emp;

    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.context = applicationContext;
    }
}
