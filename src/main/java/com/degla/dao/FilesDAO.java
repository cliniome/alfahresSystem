package com.degla.dao;

import com.degla.db.models.Clinic;
import com.degla.db.models.Employee;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.restful.models.FileModelStates;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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


    public List<PatientFile> collectFiles(Employee coordinator)
    {
        try
        {
            List<String> clinics = new ArrayList<String>();

            if(coordinator.getClinics() != null)
            {
                for(Clinic current : coordinator.getClinics())
                {
                    clinics.add(current.getClinicCode().trim());
                }
            }
            String queryString = "select f from PatientFile f where f.currentStatus.clinicCode IN (:clinics) and " +
                    " f.currentStatus.state =:state and f.currentStatus.owner.id = :id";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("clinics",clinics);
            currentQuery.setParameter("state",FileStates.DISTRIBUTED);
            currentQuery.setParameter("id",coordinator.getId());
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }

    /**
     * This method will return all patient files contained within the current container id
     * and their state is not missing
     * @param query
     * @return
     */
    public List<PatientFile> scanForFiles(String query,List<FileStates> states)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.currentStatus.containerId=:query and " +
                    "f.currentStatus.state IN (:states)";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("query",query);
            currentQuery.setParameter("states",states);

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            return new ArrayList<PatientFile>();
        }
    }

    public List<PatientFile> getFilesByStateAndEmployee(FileStates state , Employee emp)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.currentStatus.state=:state AND f.currentStatus.owner.id" +
                    " = :id";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("state",state);
            currentQuery.setParameter("id",emp.getId());

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
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
