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
@Scope("prototype")
public class AppointmentsDAO extends AbstractDAO<Appointment> {
    @Override
    public String getEntityName() {

        return "Appointment";
    }


    public List<Appointment> getTodayAppointments()
    {
        try
        {
            String queryString = "select app from Appointment app where app.active = true and app.appointment_Date >= :startDate and " +
                    "app.appointment_Date <= :endDate order by app.appointment_Date DESC";
            Date startDate = AlfahresDateUtils.getStartOfDay(new Date());
            Date endDate = AlfahresDateUtils.getEndOfDay(new Date());

            Query query = getManager().createQuery(queryString);
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
            String queryString = "select r from Appointment r where r.assignedTo.userName=:username and " +
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
