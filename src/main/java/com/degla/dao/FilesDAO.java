package com.degla.dao;

import com.degla.db.models.Clinic;
import com.degla.db.models.Employee;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
@Scope(value = "prototype")
public class FilesDAO extends AbstractDAO<PatientFile> {


    private FileStates queryState = null;

    public List<PatientFile> getFilesWithBatchNumber(String batchNumber)
    {
        try
        {
            String queryString = "select f from PatientFile f where " +
                    "f.currentStatus.batchRequestNumber = :number";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("number",batchNumber);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }


    @Override
    public List<PatientFile> getPaginatedResults(int first, int pageSize) {


        if(queryState != null)
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            currentQuery.setParameter("state",getQueryState());
            return currentQuery.getResultList();

        }else
        {
            String query = " select f from PatientFile f";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            return currentQuery.getResultList();

        }

    }

    public List<PatientFile> getFilesWithPatientName(String patientName)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.patientName = :name";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("name",patientName);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }

    public List<PatientFile> getFilesWithPatientNumber(String patientNumber)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.patientNumber = :number";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("number",patientNumber);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }

    public List<PatientFile> getFilesWithNumber(String fileNumber)
    {
        try
        {

            String queryString = "select f from PatientFile f where f.fileID = :file";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }

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

    public PatientFile getFileWithNumberAndState(String fileNumber,FileStates state)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.fileID=:file AND " +
                    "f.currentStatus.state=:state";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);
            currentQuery.setParameter("state",state);

            return (PatientFile) currentQuery.getSingleResult();

        }catch (Exception s)
        {
            return null;
        }

    }


    //TODO : the ID in the below query is not mapped correctly , please check it
    public PatientFile getSpecificFileNumberForCoordinator(String fileNumber,Employee coordinator)
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
                    " f.currentStatus.state =:state and f.fileID=:fileNumber";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("clinics",clinics);
            currentQuery.setParameter("state",FileStates.TRANSFERRED);
            currentQuery.setParameter("fileNumber",fileNumber);

            return (PatientFile) currentQuery.getSingleResult();

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }


    public List<PatientFile> collectFiles(Employee coordinator)
    {
        try
        {

            String queryString = "select f from PatientFile f where" +
                    " f.currentStatus.state =:state and f.currentStatus.owner.id = :id";
            Query currentQuery = getManager().createQuery(queryString);
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

    public List<PatientFile> receiveReceptionistFiles(String query)
    {
        try
        {
            String queryString = "select f from PatientFile f where (f.fileID=:query and " +
                    "f.currentStatus.state =:state) or (f.fileID = :query and f.currentStatus.state =:anotherstate and f.currentStatus.inpatient=:inpatient)";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("query",query);
            currentQuery.setParameter("state",FileStates.COORDINATOR_OUT);
            currentQuery.setParameter("anotherstate",FileStates.CHECKED_OUT);
            currentQuery.setParameter("inpatient",true);

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            return new ArrayList<PatientFile>();
        }
    }

    public List<PatientFile> scanForIndividualFiles(String query,List<FileStates> states)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.fileID=:query and " +
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
    public Long getFilesCountForState(FileStates state)
    {
        try
        {
            String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);

            return (Long)currentQuery.getSingleResult();

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

            String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:missing";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("missing",FileStates.MISSING);
            return (Long)currentQuery.getSingleResult();

    }

    public long getTotalCheckedOutFiles()
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_OUT);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedInFiles()
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_IN);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalTransferredFiles()
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.TRANSFERRED);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalDistributedFiles()
    {
        String query = "select count(distinct f.fileID) " +
                "from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.DISTRIBUTED);
        return (Long)currentQuery.getSingleResult();
    }


    public List<PatientFile> getFilesWithFileStates(FileStates state)
    {
        try
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:state ORDER BY f.currentStatus.createdAt DESC";

            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state", state);
            currentQuery.setMaxResults(100);
            return currentQuery.getResultList();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public List<PatientFile> getMissingFiles()
    {
        try
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:missing ORDER BY f.currentStatus.createdAt DESC";

            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("missing", FileStates.MISSING);
            currentQuery.setMaxResults(100);
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


    public FileStates getQueryState() {
        return queryState;
    }

    public void setQueryState(FileStates queryState) {
        this.queryState = queryState;
    }
}
