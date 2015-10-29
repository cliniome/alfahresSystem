package com.degla.dao;

import com.degla.utils.Paginator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */


@Transactional(propagation= Propagation.REQUIRED)
public abstract class AbstractDAO<T>{


    @PersistenceContext
    private EntityManager manager;



   /* public List<T> getPaginatedEntities(int first,int pageSize)
    {
        String query = "select t from " + getEntityName() + " t";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setFirstResult(first);
        currentQuery.setMaxResults(pageSize);

        return currentQuery.getResultList();
    }*/


    @Transactional
    public synchronized boolean addEntity(T t)
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


            EntityManager em = getManager();
            getManager().remove(em.contains(t) ? t:em.merge(t));



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

    public abstract String getEntityName();

    public  List<T> getPaginatedResults(int first , int pageSize)
    {
        String query = "select t from " + getEntityName() + " t";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setFirstResult(first);
        currentQuery.setMaxResults(pageSize);
        return currentQuery.getResultList();
    }




    public long getMaxResults()
    {
        String query = "select count(t) from " + getEntityName() + " t";
        Query currentQuery = getManager().createQuery(query);
        return Long.parseLong(currentQuery.getSingleResult().toString());
    }



}
