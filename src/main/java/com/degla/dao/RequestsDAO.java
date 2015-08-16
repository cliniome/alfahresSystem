package com.degla.dao;

import com.degla.db.models.Employee;
import com.degla.db.models.FileStates;
import com.degla.db.models.Request;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public boolean requestExistsExactlyInDB(Request request)
    {
        try
        {
            String queryString = "select count(r) from Request r where r.fileNumber = :file and r.clinicCode=:code and " +
                    "r.appointment_date_h =:date and r.appointment_time = :time and r.batchRequestNumber =:batch";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",request.getFileNumber());
            currentQuery.setParameter("code",request.getClinicCode());
            currentQuery.setParameter("date",request.getAppointment_date_h());
            currentQuery.setParameter("time",request.getAppointment_time());
            currentQuery.setParameter("batch",request.getBatchRequestNumber());

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

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }


    public List<Request> selectRequestsByDate(String username , Date date)
    {
        try
        {
            Date endofDay = getEndOfDay(date);

            String queryString = "select r from Request r where r.assignedTo.userName=:username and " +
                    "r.assignedTo.active=:state and r.appointment_Date >= :date and r.appointment_Date <= :endofDay ";

            Query currentQuery = getManager().createQuery(queryString);

            currentQuery.setParameter("username",username);
            currentQuery.setParameter("state",true);
            currentQuery.setParameter("date",date);
            currentQuery.setParameter("endofDay",endofDay);

            return currentQuery.getResultList();


        }catch(Exception s)
        {
            return null;
        }
    }

    public List<Request> getNewRequestsFor(String username)
    {
        try
        {

            String queryString = "select r from Request r where r.assignedTo.userName=:username and " +
                    "r.assignedTo.active=:state and r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state != :filestate)";

            Query currentQuery = getManager().createQuery(queryString);

            currentQuery.setParameter("username",username);
            currentQuery.setParameter("state",true);
            currentQuery.setParameter("filestate",FileStates.CHECKED_IN);

            return currentQuery.getResultList();


        }catch(Exception s)
        {
            return null;
        }
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }




    public List<Request> getPaginatedResultsByDate(int start , int end , Date chosenDate)
    {
        try {
            Date endofDay = getEndOfDay(chosenDate);
            Date startOfDay = getStartOfDay(chosenDate);
            String queryString = "select t from Request t where t.appointment_Date >= :date and t.appointment_Date <= :endofDay";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setFirstResult(start);
            currentQuery.setMaxResults(end);
            currentQuery.setParameter("date",startOfDay);
            currentQuery.setParameter("endofDay",endofDay);
            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Request>();
        }
    }


    public long getMaxResultsByDate(Date chosenDate)
    {
        try {
            Date endofDay = getEndOfDay(chosenDate);
            Date startOfDay = getStartOfDay(chosenDate);
            String queryString = "select count(t) from Request t where t.appointment_Date >= :date and t.appointment_Date <= :endofDay";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("date",startOfDay);
            currentQuery.setParameter("endofDay",endofDay);
            return (Long)currentQuery.getSingleResult();


        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
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
