package com.degla.restful;

import com.degla.db.models.Employee;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by snouto on 22/05/15.
 */
public class BasicRestful {





    protected Employee getAccount()
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null) return null;

            Employee emp = (Employee)authentication.getPrincipal();

            if(emp == null) return null;


            //Don't allow deleted or inactive employees to login to mobile devices
            if(!emp.isAccountNonLocked()) return null;
            if(!emp.isActive()) return null;


            return emp;


        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }

    }
}
