package com.degla.dao;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;

/**
 * Created by snouto on 02/05/2015.
 */


@Transactional(propagation= Propagation.REQUIRED)
public abstract class AbstractDAO<T> {


    @PersistenceContext
    private EntityManager manager;


    @Transactional
    public  boolean addEntity(T t)
    {
        try
        {


            getManager().persist(t);



            return true;


        }catch(Exception s)
        {
            System.out.println(s.getMessage());
            return false;
        }
    }

    @Transactional
    public  boolean removeEntity(T t)
    {
        try
        {


            getManager().remove(t);



            return true;

        }catch(Exception s)
        {

            return false;


        }
    }


    public  boolean removeEntityByID(int uuid)
    {
        try
        {
            @SuppressWarnings("unchecked")
            T t = (T) getManager().find(getGenericType()	, uuid);

            return removeEntity(t);


        }catch(Exception s)
        {

            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public Class getGenericType() {

        ParameterizedType parameterizedType = (ParameterizedType)getClass()
                .getGenericSuperclass();
        return (Class) parameterizedType.getActualTypeArguments()[0];
    }


    @SuppressWarnings("unchecked")
    public  T getEntity(int uuid)
    {
        return (T) getManager().find(getGenericType(), uuid);
    }


    @Transactional
    public  boolean updateEntity(T t)
    {
        try
        {




            getManager().merge(t);




            return true;

        }catch(Exception s)
        {

            return false;
        }
    }


    protected synchronized EntityManager getManager()
    {
        return this.manager;
    }


    public AbstractDAO()
    {
        //load the jdbc Driver
        try {

            //initializeDAO();
        } catch (Exception e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }


    }












}
