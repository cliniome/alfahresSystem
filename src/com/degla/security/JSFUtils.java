package com.degla.security;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public final class JSFUtils {
	
	
	  @SuppressWarnings("unchecked")
	public static Object getAnyBeanByName(String beanName,Class type) {
		    try {

		      FacesContext context = FacesContext.getCurrentInstance();
		        Application currentApp = context.getApplication();
		        
		        
		        return currentApp.evaluateExpressionGet(context, "#{" +beanName+"}", type);
		      
		    }catch(Exception s) {
		      s.printStackTrace();
		      return null;
		    }
		  }

}