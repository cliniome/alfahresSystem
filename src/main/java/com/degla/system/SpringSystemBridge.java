package com.degla.system;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 08/05/2015.
 */
@Component("systemBridge")
public class SpringSystemBridge implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        context = applicationContext;
    }


    public static SystemService services() throws Exception
    {
        SystemService  systemService = context.getBean(SystemService.class);
        return systemService;
    }
}
