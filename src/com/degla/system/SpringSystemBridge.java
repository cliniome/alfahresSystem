package com.degla.system;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 08/05/2015.
 */
@Component("systemBridge")
public class SpringSystemBridge implements ApplicationContextAware {

    private static ApplicationContext context;
    static SystemService systemService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        context = applicationContext;
    }


    public static SystemService services() throws Exception
    {
        if(systemService == null)
            systemService = context.getBean(SystemService.class);
        return systemService;
    }
}
