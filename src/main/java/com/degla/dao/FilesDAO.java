package com.degla.dao;

import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
public class FilesDAO extends AbstractDAO<PatientFile> {



    public PatientFile getFileWithNumber(String fileNumber)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.fileID=:file";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);
            return (PatientFile) currentQuery.getSingleResult();

        }catch (Exception s)
        {
            return null;
        }

    }

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


    public List<PatientFile> searchFiles(String query) throws Exception
    {
        String queryString = "select f from PatientFile f where f.fileID=:query or f.patientNumber=:query";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("query",query);

        return currentQuery.getResultList();
    }

    public long getTotalNewFiles()
    {
       /* String query = "select count(f) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.NEW);
        return (Long)currentQuery.getSingleResult();*/
        return -1;
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
