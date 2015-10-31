package com.degla.dao;

import com.degla.db.models.Appointment;
import com.degla.db.models.FileStates;
import com.degla.restful.utils.AlfahresDateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 20/09/15.
 */
@Component("appointmentsDAO")
@Scope("singleton")
public class AppointmentsDAO extends AbstractDAO<Appointment> {
    @Override
    public String getEntityName() {

        return "Appointment";
    }



    public List<Appointment> getAppointmentsForDate(Date chosenDate)
    {
        String queryString = "select r from Appointment r where r.appointment_Date >= :start and r.appointment_Date <= :end and r.active = true";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(chosenDate));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(chosenDate));

        return currentQuery.getResultList();
    }


    public List<Object[]> getAppointmentsMetaData(Date start,Date end)
    {
        String queryString = "select distinct r.clinicCode ,count(distinct r.fileNumber),r.clinicName from Appointment r where r.appointment_Date >= :start and r.appointment_Date <= :end and r.active = true " +
                " group by r.clinicCode , r.clinicName";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(start));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(end));

        return currentQuery.getResultList();
    }

    public List<Appointment> getAppointmentsForDateRange(Date start,Date end)
    {
        /*
            not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate " +
                    " and p.fileID = r.fileNumber)";
         */
        String queryString = "select r from Appointment r where r.appointment_Date >= :start and r.appointment_Date <= :end and r.active = true";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(start));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(end));

        return currentQuery.getResultList();
    }


    public List<Appointment> searchAppointments(String query)
    {
        String queryString = "select r from Appointment r where r.fileNumber=:query or r.patientNumber =:query";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("query",query);
        currentQuery.setMaxResults(300);

        return currentQuery.getResultList();
    }


    public List<Appointment> getMostRecentAppointmentsFor(String fileNumber)
    {
        String queryString = "select r from Appointment r where r.fileNumber=:query order by r.appointment_Date DESC";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("query",fileNumber);
        currentQuery.setMaxResults(10);

        return currentQuery.getResultList();
    }

    public boolean hasTransfer(String fileNumber , Date appointmentDate)
    {
        try
        {
            String queryString = "select count(app) from Appointment app where app.fileNumber = :number and app.appointment_Date >= :startdate and " +
                    "app.appointment_Date <=:enddate and app.active = true";

            Query currentQuery = getManager().createQuery(queryString);

            Date startOfDay = AlfahresDateUtils.getStartOfDay(appointmentDate);
            Date endofDay = AlfahresDateUtils.getEndOfDay(appointmentDate);

            currentQuery.setParameter("number",fileNumber);
            currentQuery.setParameter("startdate",startOfDay);
            currentQuery.setParameter("enddate",endofDay);

            return (((Long)currentQuery.getSingleResult()) > 0 ? true : false);

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }


    public List<Appointment> getTransfersFor(String fileNumber,Date appointmentDate)
    {
        try
        {
            String queryString = "select app from Appointment app where app.fileNumber = :number and app.appointment_Date >= :startdate and " +
                    "app.appointment_Date <= :enddate and app.active = true";

            Query currentQuery = getManager().createQuery(queryString);

            Date startOfDay = AlfahresDateUtils.getStartOfDay(appointmentDate);
            Date endofDay = AlfahresDateUtils.getEndOfDay(appointmentDate);

            currentQuery.setParameter("number",fileNumber);
            currentQuery.setParameter("startdate",startOfDay);
            currentQuery.setParameter("enddate",endofDay);

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public List<Appointment> getNewRequestsFor(String username)
    {
        try
        {

            String queryString = "select distinct(r) from Appointment r where r.active=true and r.assignedTo.userName=:username and " +
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

    public  long getRequestsCountFor(String username)
    {

        try
        {
            String queryString = "select count(r) from Appointment r where r.active=true and r.assignedTo.userName=:username and " +
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

    public long getTotalWatchList()
    {
        return getCountOfWatchListRequests();
    }

    public List<Appointment> getAllWatchListRequests()
    {
        try
        {
            String queryString = "select r from Appointment r where r.active = true and " +
                    " r.fileNumber  not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate and " +
                    "p.fileID = r.fileNumber) and r.fileNumber in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber)";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("filestate", FileStates.CHECKED_IN);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Appointment>();
        }
    }

    public boolean appointmentExistsBefore(Appointment appointment)
    {
        try
        {

            String queryString = "select count(app) from Appointment app where app.fileNumber = :number and " +
                    "app.patientNumber = :patientNumber and " +
                    " app.appointment_Date = :date and app.clinicCode = :code";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("number",appointment.getFileNumber());
            currentQuery.setParameter("patientNumber",appointment.getPatientNumber());
            currentQuery.setParameter("date",appointment.getAppointment_Date());
            currentQuery.setParameter("code",appointment.getClinicCode());

            return ((Long)currentQuery.getSingleResult() > 0 ? true : false);


        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    public long getTotalNewAppointments()
    {
        String queryString = "select count(r) from Appointment r where r.active = true and " +
                " r.fileNumber in (select p.fileID from PatientFile p where p.currentStatus.state =:filestate) or " +
                " r.fileNumber not in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber)";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
        return (Long)currentQuery.getSingleResult();
    }


    public long getTotalNewAppointments_ByClinicCode(String clinicCode)
    {
        String queryString = "select count(r) from Appointment r where r.active = true and " +
                " r.fileNumber in (select p.fileID from PatientFile p where p.currentStatus.state =:filestate) or " +
                " r.fileNumber not in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber) and r.clinicCode = :code";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
        currentQuery.setParameter("code",clinicCode);
        return (Long)currentQuery.getSingleResult();
    }

    public long getTotalNewAppointments_AppointmentDate(Date date)
    {
        String queryString = "select count(r) from Appointment r where r.active = true and " +
                " r.fileNumber in (select p.fileID from PatientFile p where p.currentStatus.state =:filestate) or " +
                " r.fileNumber not in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber) and " +
                "r.appointment_Date >= :start and r.appointment_Date <= :end";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(date));
        return (Long)currentQuery.getSingleResult();
    }


    public long getMaxResultsByDate(Date chosenDate)
    {
        try {
            Date endofDay = AlfahresDateUtils.getEndOfDay(chosenDate);
            Date startOfDay = AlfahresDateUtils.getStartOfDay(chosenDate);
            String queryString = "select count(t) from Appointment t where t.active = true and t.appointment_Date >= :date and t.appointment_Date <= :endofDay";
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


    @Override
    public List<Appointment> getPaginatedResults(int first, int pageSize) {

        try
        {
            String queryString = "select r from Appointment r where r.active = true and " +
                    " r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state != :filestate)";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("filestate", FileStates.CHECKED_IN);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Appointment>();
        }
    }

    @Override
    public long getMaxResults() {

        String queryString = "select count(r) from Appointment r where r.active = true and " +
                " r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state !=:filestate)";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate", FileStates.CHECKED_IN);
        return Long.parseLong(currentQuery.getSingleResult().toString());
    }

    public long getCountOfWatchListRequests()
    {
        String queryString = "select count(r) from Appointment r where r.active=true and r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate and " +
                "p.fileID = r.fileNumber) and r.fileNumber in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber)";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);

        return Long.parseLong(currentQuery.getSingleResult().toString());
    }

    public long getCountOfWatchListRequests_ByClinicCode(String code)
    {
        String queryString = "select count(r) from Appointment r where r.active=true and r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate and " +
                "p.fileID = r.fileNumber) and r.fileNumber in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber) and r.clinicCode = :code";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
        currentQuery.setParameter("code",code);
        return Long.parseLong(currentQuery.getSingleResult().toString());
    }

    public long getCountOfWatchListRequests_AppointmentDate(Date date)
    {
        String queryString = "select count(r) from Appointment r where r.active=true and r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate and " +
                "p.fileID = r.fileNumber) and r.fileNumber in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber) and " +
                "r.appointment_Date >= :start and r.appointment_Date <= :end";
        Query currentQuery = getManager().createQuery(queryString);
        currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
        currentQuery.setParameter("start",AlfahresDateUtils.getStartOfDay(date));
        currentQuery.setParameter("end",AlfahresDateUtils.getEndOfDay(date));

        return Long.parseLong(currentQuery.getSingleResult().toString());
    }

    public List<Appointment> getAllWatchListRequests(int first , int pageSize)
    {
        try
        {
            String queryString = "select r from Appointment r where r.active = true and " +
                    "r.fileNumber not in (select p.fileID from PatientFile p where p.currentStatus.state = :filestate " +
                    "and p.fileID = r.fileNumber) and r.fileNumber in (select pf.fileID from PatientFile pf where pf.fileID = r.fileNumber)";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("filestate",FileStates.CHECKED_IN);
            currentQuery.setFirstResult(first);
            currentQuery.setMaxResults(pageSize);
            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Appointment>();
        }
    }


    public List<Appointment> getTodayAppointments(String fileNumber)
    {
        try
        {
            String queryString = "select app from Appointment app where app.fileNumber = :fileNumber and app.active = true and app.appointment_Date >= :startDate and " +
                    "app.appointment_Date <= :endDate order by app.appointment_Date DESC";
            Date startDate = AlfahresDateUtils.getStartOfDay(new Date());
            Date endDate = AlfahresDateUtils.getEndOfDay(new Date());

            Query query = getManager().createQuery(queryString);
            query.setParameter("fileNumber",fileNumber);
            query.setParameter("startDate",startDate);
            query.setParameter("endDate",endDate);

            return query.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Appointment>();
        }
    }


    public List<Appointment> selectAppointmentsByDate(String username , Date date)
    {
        try
        {
            String queryString = "select r from Appointment r where r.active = true and r.assignedTo.userName=:username and " +
                    "r.assignedTo.active=:state and r.appointment_Date >= :date and r.appointment_Date <= :endofDay and r.fileNumber not in " +
                    " (select p.fileID from PatientFile p where p.currentStatus.state != :filestate) ";

            Date startDate = AlfahresDateUtils.getStartOfDay(date);
            Date endDate = AlfahresDateUtils.getEndOfDay(date);
            Query currentQuery = getManager().createQuery(queryString);

            currentQuery.setParameter("username",username);
            currentQuery.setParameter("state",true);
            currentQuery.setParameter("date",startDate);
            currentQuery.setParameter("endofDay",endDate);
            currentQuery.setParameter("filestate", FileStates.CHECKED_IN);

            return currentQuery.getResultList();



        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }
}
