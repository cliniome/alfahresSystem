package com.degla.dao;

import com.degla.db.models.*;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.utils.AlfahresDateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import test.AlfahresJsonBuilder;

import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
@Scope(value = "prototype")
public class FilesDAO extends AbstractDAO<PatientFile> {


    private FileStates queryState = null;

    private Date appointmentDate;

    private boolean inWatchList;

    private List<Appointment> availableAppointments;

    public List<PatientFile> getFilesWithBatchNumber(String batchNumber)
    {
        try
        {
            String queryString = "select f from PatientFile f where " +
                    "f.currentStatus.appointment.batchRequestNumber = :number";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("number",batchNumber);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }


    private List<String> getFileNumbers()
    {
        List<String> fileNumbers = new ArrayList<String>();

        for(Appointment app : getAvailableAppointments())
        {
            fileNumbers.add(app.getFileNumber());
        }

        return fileNumbers;
    }


    @Override
    public List<PatientFile> getPaginatedResults(int first, int pageSize) {


        if(isInWatchList())
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:state and " +
                    "f.fileID in  (:fileNumbers) or (f.currentStatus.appointment.appointment_Date >= :start and f.currentStatus.appointment.appointment_Date <= :end" +
                    " and f.currentStatus.state = :state)";

            Query currentQuery = getManager().createQuery(query);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            currentQuery.setParameter("state", getQueryState());
            currentQuery.setParameter("fileNumbers",getFileNumbers());
            currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(getAppointmentDate()));
            currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(getAppointmentDate()));
            return currentQuery.getResultList();

        }else if(queryState != null && getAppointmentDate() != null)
        {
            String query = "select f from PatientFile f where f.currentStatus.state=:state " +
                    "and f.currentStatus.appointment.appointment_Date >= :startdate " +
                    "and f.currentStatus.appointment.appointment_Date <= :enddate";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            currentQuery.setParameter("state", getQueryState());
            currentQuery.setParameter("startdate", AlfahresDateUtils.getStartOfDay(getAppointmentDate()));
            currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getAppointmentDate()));
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

    public boolean fileExists(String fileNumber)
    {
        try
        {

            String queryString = "select count (distinct f.fileID) from PatientFile f where f.fileID = :file";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);

            long number = (Long)currentQuery.getSingleResult();

            if(number > 0) return true;
            else return false;

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
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
                    "f.currentStatus.state=:state and f.currentStatus.appointment.inpatient = false";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("file",fileNumber);
            currentQuery.setParameter("state",state);

            return (PatientFile) currentQuery.getSingleResult();

        }catch (Exception s)
        {
            return null;
        }

    }

    public PatientFile getInpatientFileWithNumberAndState(String fileNumber , FileStates state)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.fileID=:file AND " +
                    "f.currentStatus.state=:state and f.currentStatus.appointment.inpatient = true";
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

            String queryString = "select f from PatientFile f where f.currentStatus.appointment.clinicCode IN (:clinics) and " +
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


    public List<PatientFile> collectFiles(Employee coordinator,Date serverTimeStamp)
    {
        try
        {

            String queryString = "select f from PatientFile f where" +
                    " f.currentStatus.state =:state and f.currentStatus.owner.id = :id and f.currentStatus.createdAt > :serverTimeStamp";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("state",FileStates.DISTRIBUTED);
            currentQuery.setParameter("id",coordinator.getId());
            currentQuery.setParameter("serverTimeStamp",serverTimeStamp);
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
                    "f.currentStatus.state =:state) or (f.fileID = :query and f.currentStatus.state =:anotherstate and f.currentStatus.appointment.inpatient=:inpatient)";
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

    public List<PatientFile> scanForInpatientFiles(String query , List<FileStates> states)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.fileID=:query and f.currentStatus.appointment.inpatient = true and " +
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

    public List<PatientFile> getFilesByStateAndEmployee(FileStates state , Employee emp , Date operationDate)
    {
        try
        {
            String queryString = "select f from PatientFile f where f.currentStatus.state=:state AND f.currentStatus.owner.id" +
                    " = :id and f.currentStatus.createdAt > :operationDate";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("state",state);
            currentQuery.setParameter("id",emp.getId());
            currentQuery.setParameter("operationDate",operationDate);

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
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

    public Long getFilesCountForState_ByClinic(FileStates state,String clinicCode)
    {
        try
        {
            String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and " +
                    "f.currentStatus.appointment.clinicCode = :code";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);
            currentQuery.setParameter("code",clinicCode);
            return (Long)currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();;
            return null;
        }
    }



    public Long getFilesCountForState_AppointmentDate(FileStates state,Date date)
    {
        try
        {
            String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and " +
                    "f.currentStatus.appointment.appointment_Date >= :start and f.currentStatus.appointment.appointment_Date <= :end";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);
            currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
            currentQuery.setParameter("end", AlfahresDateUtils.getEndOfDay(date));

            return (Long)currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();;
            return null;
        }
    }

    public Long getFilesCountForState_OperationDate(FileStates state,Date date)
    {
        try
        {
            String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and " +
                    "f.currentStatus.createdAt >= :start and f.currentStatus.createdAt <= :end";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state",state);
            currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
            currentQuery.setParameter("end", AlfahresDateUtils.getEndOfDay(date));

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

    public long getTotalCheckedOutByType(boolean inpatient)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and f.currentStatus.appointment.inpatient = :inpatient";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_OUT);
        currentQuery.setParameter("inpatient",inpatient);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedOutFiles_AppointmentDate(Date date)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and " +
                "f.currentStatus.appointment.appointment_Date >= :start and f.currentStatus.appointment.appointment_Date <= :end";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_OUT);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(date));
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedOutFiles_OperationDate(Date date)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and " +
                "f.currentStatus.createdAt >= :start and f.currentStatus.createdAt <= :end";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_OUT);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(date));
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedInFiles()
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_IN);
        return (Long)currentQuery.getSingleResult();
    }


    public long getTotalCheckedInFiles_OperationDate(Date operationDate)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and f.currentStatus.createdAt >= :start and f.currentStatus.createdAt <= :end";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_IN);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(operationDate));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(operationDate));
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedInFiles_AppointmentDate(Date appointmentDate)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and f.currentStatus.appointment.appointment_Date >= :start and " +
                "f.currentStatus.appointment.appointment_Date <= :end";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_IN);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(appointmentDate));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(appointmentDate));
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalCheckedInFiles_ByClinic(String clinicCode)
    {
        String query = "select count(distinct f.fileID) from PatientFile f where f.currentStatus.state=:state and f.currentStatus.appointment.clinicCode = :code";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state",FileStates.CHECKED_IN);
        currentQuery.setParameter("code",clinicCode);
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

    public List<PatientFile> getPaginatedWrappedData()
    {
        try
        {
            if(isInWatchList())
            {
                String query = "select f from PatientFile f where f.currentStatus.state=:state and " +
                        "f.currentStatus.appointment.appointment_Date >= :startdate " +
                        "and f.currentStatus.appointment.appointment_Date <= :enddate and " +
                        "f.fileID in (select app.fileNumber from Appointment app where app.active=true)";

                Query currentQuery = getManager().createQuery(query);
                currentQuery.setParameter("state", getQueryState());
                currentQuery.setParameter("startdate", AlfahresDateUtils.getStartOfDay(getAppointmentDate()));
                currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getAppointmentDate()));
                return currentQuery.getResultList();

            }else if(getAppointmentDate() != null && getQueryState() != null)
            {
                String query = "select f from PatientFile f where f.currentStatus.state=:state " +
                        "and f.currentStatus.appointment.appointment_Date >= :startdate " +
                        "and f.currentStatus.appointment.appointment_Date <= :enddate";

                Query currentQuery = getManager().createQuery(query);
                currentQuery.setParameter("state",getQueryState());
                currentQuery.setParameter("startdate",AlfahresDateUtils.getStartOfDay(getAppointmentDate()));
                currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getAppointmentDate()));

                return currentQuery.getResultList();

            }else return null;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public List<PatientFile> getMissingFiles(boolean inpatient)
    {
        try
        {
            String query = null;

            if(inpatient)
                query = "select f from PatientFile f where f.currentStatus.state=:missing and f.currentStatus.appointment.inpatient = true" +
                        " ORDER BY f.currentStatus.createdAt DESC";
            else
             query = "select f from PatientFile f where f.currentStatus.state=:missing ORDER BY f.currentStatus.createdAt DESC";

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
    public long getMaxResults()
    {
        try
        {
            if(getQueryState() != null && getAppointmentDate() != null)
            {
                String queryString = "select count(f) from PatientFile f where f.currentStatus.state=:state and " +
                        "f.currentStatus.appointment.appointment_Date >= :startdate and f.currentStatus.appointment.appointment_Date <= :enddate " +
                        "ORDER BY f.currentStatus.createdAt DESC";
                Query currentQuery = getManager().createQuery(queryString);

                currentQuery.setParameter("state",getQueryState());

                currentQuery.setParameter("startdate",AlfahresDateUtils.getStartOfDay(getAppointmentDate()));
                currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getAppointmentDate()));

                return (Long)currentQuery.getSingleResult();

            }else
            {
                return super.getMaxResults();
            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0;
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

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isInWatchList() {
        return inWatchList;
    }

    public void setInWatchList(boolean inWatchList) {
        this.inWatchList = inWatchList;
    }

    public List<Appointment> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(List<Appointment> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }
}
