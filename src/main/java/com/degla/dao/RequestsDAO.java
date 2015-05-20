package com.degla.dao;

import com.degla.db.models.Request;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 15/05/15.
 */
@Component("requestsDAO")
public class RequestsDAO extends  AbstractDAO<Request> {

    @Override
    public String getEntityName() {
        return "Request";
    }


    public Request getSingleRequest(String fileNumber)
    {
        String queryString = "select r from Request r where r.fileNumber =:file";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("file",fileNumber);

        List<Request> requests = currentQuery.getResultList();

        if(requests == null || requests.size() <=0)
            throw new EntityNotFoundException("Current File does not exist");

        return requests.get(0);
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
}
