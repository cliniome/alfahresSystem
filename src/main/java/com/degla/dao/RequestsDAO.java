package com.degla.dao;

import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 15/05/15.
 */
@Component("requestsDAO")
@Scope("prototype")
public class RequestsDAO extends  AbstractDAO<Request> {

    @Override
    public String getEntityName() {
        return "Request";
    }


    public Request getRequestByBatchNumber(String fileNumber , String batchNumber)
    {
        try
        {
            String queryString = "select r from Request r where r.fileNumber=:file AND " +
                    " r.batchRequestNumber=:batch";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);
            currentQuery.setParameter("batch",batchNumber);

            return (Request) currentQuery.getSingleResult();

        }catch (Exception s)
        {
            return null;
        }
    }

    public boolean requestExistsBasedOnAllInfo(Request request)
    {
        try
        {
            String queryString = "select count(r) from Request r where r.fileNumber = :file and r.clinicCode=:code and " +
                    "r.appointment_date_h =:date and r.appointment_time = :time";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",request.getFileNumber());
            currentQuery.setParameter("code",request.getClinicCode());
            currentQuery.setParameter("date",request.getAppointment_date_h());
            currentQuery.setParameter("time",request.getAppointment_time());

            long count = (Long)currentQuery.getSingleResult();

            return ((count > 0) ? true: false);

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }


    public boolean requestExists(String fileNumber)
    {
        try
        {
            String queryString = "select count(r) from Request r where r.fileNumber = :file";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);

            long count = (Long)currentQuery.getSingleResult();

            return ((count > 0) ? true: false);

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }



    public Request getSingleRequest(String fileNumber)
    {
       try
       {
           String queryString = "select r from Request r where r.fileNumber =:file";
           Query currentQuery = getManager().createQuery(queryString);
           currentQuery.setParameter("file",fileNumber);

           List<Request> requests = currentQuery.getResultList();

           if(requests == null || requests.size() <=0)
               throw new EntityNotFoundException("Current File does not exist");

           return requests.get(0);

       }catch (Exception s)
       {
           return null;
       }
    }

    public List<Request> searchRequests(String query)
    {
        String queryString = "select r from Request r where r.fileNumber=:query or r.patientNumber =:query";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("query",query);

        return currentQuery.getResultList();
    }


    public List<Request> getAllRequests()
    {
        try
        {
            String queryString ="select r from Request r";
            Query currentQuery = getManager().createQuery(queryString);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public long getTotalNewRequests()
    {
        String queryString = "select count(r) from Request r";
        Query currentQuery = getManager().createQuery(queryString);
        return (Long)currentQuery.getSingleResult();

    }

    public List<Request> getNewRequestsFor(String username)
    {
        try
        {
            String queryString = "select r from Request r where r.assignedTo.userName=:username and " +
                    "r.assignedTo.active=:state";

            Query currentQuery = getManager().createQuery(queryString);

            currentQuery.setParameter("username",username);
            currentQuery.setParameter("state",true);

            return currentQuery.getResultList();


        }catch(Exception s)
        {
            return null;
        }
    }

    public long getRequestsCountFor(String username)
    {

        try
        {
            String queryString = "select count(r) from Request r where r.assignedTo.userName=:username and " +
                    "r.assignedTo.active=:state";

            Query currentQuery = getManager().createQuery(queryString);

            currentQuery.setParameter("username",username);
            currentQuery.setParameter("state",true);

            return (Long)currentQuery.getSingleResult();


        }catch(Exception s)
        {
            return Long.MIN_VALUE;
        }
    }
}
