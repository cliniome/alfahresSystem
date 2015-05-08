package com.degla.dao;

import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
public class FilesDAO extends AbstractDAO<PatientFile> {


    public List<PatientFile> getFilesWithState(FileStates state)
    {
        try
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);

            return currentQuery.getResultList();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }
    public Integer getFilesCountForState(FileStates state)
    {
        try
        {
            String query = "select count(f) from PatientFile f where f.currentStatus.state=:state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);

            return (Integer)currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();;
            return null;
        }
    }

    public long getTotalNewFiles()
    {
        String query = "select count(f) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.NEW);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalMissingFiles()
    {

            String query = "select count(f) from PatientFile f where f.currentStatus.state=:missing";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("missing",FileStates.MISSING);
            return (Long)currentQuery.getSingleResult();

    }
    public List<PatientFile> getMissingFiles()
    {
        try
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:missing";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("missing", FileStates.MISSING);

            return currentQuery.getResultList();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }


    @Override
    public String getEntityName() {
        return "PatientFile";
    }


}
