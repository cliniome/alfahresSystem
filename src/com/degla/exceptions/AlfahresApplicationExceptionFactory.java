package com.degla.exceptions;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Created by snouto on 04/05/2015.
 */
public class AlfahresApplicationExceptionFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory factory;

    public AlfahresApplicationExceptionFactory(ExceptionHandlerFactory fact)
    {
        this.factory = fact;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {

        AlfahresApplicationExceptionHandler handler = new AlfahresApplicationExceptionHandler(factory.getExceptionHandler());

        return handler;
    }
}
