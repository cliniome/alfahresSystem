package com.degla.dao;

import com.degla.dao.utils.SearchSettings;
import com.degla.db.models.Appointment;
import com.degla.db.models.PatientFile;
import com.degla.restful.utils.AlfahresDateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import static com.degla.dao.utils.SearchSettings.*;

/**
 * Created by snouto on 09/10/15.
 */
@Component
@Scope("prototype")
public class PrintFilesDAO extends AbstractDAO<PatientFile> {


    private SearchSettings searchSettings;
    private List<Appointment> availableAppointments;


    @Override
    public String getEntityName() {
        return "PatientFile";
    }


    private List<PatientFile> getPaginatedAllFiles(int first , int pageSize)
    {
        try
        {

            String query = " select f from PatientFile f where f.currentStatus.state = :state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state", getSearchSettings().getStatus());
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }

    private List<PatientFile> getPaginatedAllFilesWithoutPagination()
    {
        try
        {

            String query = " select f from PatientFile f where f.currentStatus.state = :state";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("state", getSearchSettings().getStatus());
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }


    private List<PatientFile> getPaginatedFilesByAppointmentDate(int first , int pageSize)
    {
        String query = "select f from PatientFile f where f.currentStatus.state=:state " +
                "and f.currentStatus.appointment.appointment_Date >= :startdate " +
                "and f.currentStatus.appointment.appointment_Date <= :enddate";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setFirstResult(first);
        currentQuery.setMaxResults(pageSize);
        currentQuery.setParameter("state", getSearchSettings().getStatus());
        currentQuery.setParameter("startdate", AlfahresDateUtils.getStartOfDay(getSearchSettings().getAppointmentDate()));
        currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getSearchSettings().getAppointmentDate()));
        return currentQuery.getResultList();
    }

    private List<PatientFile> getPaginatedFilesByAppointmentDateWithoutPagination()
    {
        String query = "select f from PatientFile f where f.currentStatus.state=:state " +
                "and f.currentStatus.appointment.appointment_Date >= :startdate " +
                "and f.currentStatus.appointment.appointment_Date <= :enddate";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state", getSearchSettings().getStatus());
        currentQuery.setParameter("startdate", AlfahresDateUtils.getStartOfDay(getSearchSettings().getAppointmentDate()));
        currentQuery.setParameter("enddate",AlfahresDateUtils.getEndOfDay(getSearchSettings().getAppointmentDate()));
        return currentQuery.getResultList();
    }


    private List<PatientFile> getPaginatedFilesByAppointmentDateAndWatchList(int first , int pageSize)
    {
        String query = "select f from PatientFile f where f.currentStatus.state=:state and " +
                "f.fileID in  (:fileNumbers) or (f.currentStatus.appointment.appointment_Date >= :start and f.currentStatus.appointment.appointment_Date <= :end" +
                " and f.currentStatus.state = :state)";

        Query currentQuery = getManager().createQuery(query);
        currentQuery.setFirstResult(first);
        currentQuery.setMaxResults(pageSize);
        currentQuery.setParameter("state", getSearchSettings().getStatus());
        currentQuery.setParameter("fileNumbers",getFileNumbers());
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(getSearchSettings().getAppointmentDate()));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(getSearchSettings().getAppointmentDate()));
        return currentQuery.getResultList();
    }

    private List<PatientFile> getPaginatedFilesByAppointmentDateAndWatchListWithoutPagination()
    {
        String query = "select f from PatientFile f where f.currentStatus.state=:state and " +
                "f.fileID in  (:fileNumbers) or (f.currentStatus.appointment.appointment_Date >= :start and f.currentStatus.appointment.appointment_Date <= :end" +
                " and f.currentStatus.state = :state)";

        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("state", getSearchSettings().getStatus());
        currentQuery.setParameter("fileNumbers",getFileNumbers());
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(getSearchSettings().getAppointmentDate()));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(getSearchSettings().getAppointmentDate()));
        return currentQuery.getResultList();
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
        try
        {
            if(getSearchSettings() == null) throw new Exception("Search Settings can't be null");

            switch (getSearchSettings().getType())
            {
                case DISPLAY_ALL:
                {
                    return this.getPaginatedAllFiles(first,pageSize);
                }
                case DISPLAY_BY_APPOINTMENT:
                {
                    if(getSearchSettings().isWatchlist())
                    {
                        return this.getPaginatedFilesByAppointmentDateAndWatchList(first,pageSize);
                    }else
                    {
                        return this.getPaginatedFilesByAppointmentDate(first,pageSize);
                    }
                }

                default:
                    return new ArrayList<PatientFile>();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }

    }


    public List<PatientFile> getPaginatedWrappedData()
    {
        try
        {
            switch (getSearchSettings().getType())
            {
                case DISPLAY_ALL:
                {
                    return this.getPaginatedAllFilesWithoutPagination();
                }
                case DISPLAY_BY_APPOINTMENT:
                {
                    if(getSearchSettings().isWatchlist())
                    {
                        return this.getPaginatedFilesByAppointmentDateAndWatchListWithoutPagination();
                    }else
                    {
                        return this.getPaginatedFilesByAppointmentDateWithoutPagination();
                    }
                }

                default:
                    return new ArrayList<PatientFile>();

            }

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }


    public List<Appointment> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(List<Appointment> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }

    public SearchSettings getSearchSettings() {
        return searchSettings;
    }

    public void setSearchSettings(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
    }
}
