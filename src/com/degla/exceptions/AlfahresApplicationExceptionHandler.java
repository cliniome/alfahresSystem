package com.degla.exceptions;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;

/**
 * Created by snouto on 04/05/2015.
 */
public class AlfahresApplicationExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler wrapped;

    public AlfahresApplicationExceptionHandler(ExceptionHandler handler)
    {
        this.wrapped = handler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {

        //getting the facesContext
        FacesContext fc = FacesContext.getCurrentInstance();

        //get exceptions
        Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

        while(iterator.hasNext())
        {
            //get the unhandledExceptionQueueEvent
            ExceptionQueuedEvent event = (ExceptionQueuedEvent)iterator.next();

            //from the event we will get the exception Context
            ExceptionQueuedEventContext context = event.getContext();

            //get the raised exception in here
            Throwable exception = context.getException();

            //you can log the Unhandled Exception in your entire web application in here
            //try to redirect the user to the errors.xhtml
            try
            {
                NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(fc,null,"error?faces-redirect=true");
            }finally {
                iterator.remove(); // remove the current Exception from The Exceptions Queue

            }

        }

        //finally at the end , call the parent handle method to handle other events.
        getWrapped().handle();
    }
}
