package com.degla.security;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringUtils {
	
	
	public static Object getSpringBean(String name)
	{
		try
		{
			FacesContext context = FacesContext.getCurrentInstance();

			ApplicationContext springContext = WebApplicationContextUtils
					.getWebApplicationContext((ServletContext) context
							.getExternalContext().getContext());
			
			
			return springContext.getBean(name);
			
		}catch(Exception s)
		{
			
			return null;
		}
	}

}