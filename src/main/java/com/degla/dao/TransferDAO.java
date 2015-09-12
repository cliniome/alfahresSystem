package com.degla.dao;

import com.degla.db.models.Transfer;
import com.degla.restful.utils.AlfahresDateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by snouto on 20/06/15.
 */
@Component("transferDAO")
@Scope("prototype")
public class TransferDAO extends AbstractDAO<Transfer> {
    @Override
    public String getEntityName() {
        return "Transfer";
    }

    public boolean hasTransfer(String fileNumber)
    {
        try
        {
            long transfersCount = this.getTransfersCount(fileNumber);

            if(transfersCount > 0) return true;
            else return false;

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    public boolean hasTransferInTheSameDay(String fileNumber , Date date)
    {
        try
        {
            long transfersCountByDate = this.getTransfersCountByDate(fileNumber, date);

            if(transfersCountByDate > 0) return true;
            else return false;

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    private long getTransfersCountByDate(String fileNumber , Date date)
    {
        try
        {

            Date startDate = AlfahresDateUtils.getStartOfDay(date);
            Date endDate = AlfahresDateUtils.getEndOfDay(date);

            String queryString = "select count(t.fileNumber) from Transfer t where t.fileNumber = :fileNumber and t.appointment_Date_G >= :start and " +
                    "t.appointment_Date_G <= :end";

            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("start",startDate);
            currentQuery.setParameter("end",endDate);
            return (Long)currentQuery.getSingleResult();


        }catch (Exception s)
        {
            s.printStackTrace();
            return 0;
        }
    }

    private long getTransfersCount(String fileNumber) {


        try
        {
            Date todayDate = new Date();
            Date startDate = AlfahresDateUtils.getStartOfDay(todayDate);
            Date endDate = AlfahresDateUtils.getEndOfDay(todayDate);
            String queryString = "select count(t.fileNumber) from Transfer t where t.fileNumber = :fileNumber and t.appointment_Date_G >= :start " +
                    "and t.appointment_Date_G <= :end ";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("start",startDate);
            currentQuery.setParameter("end",endDate);
            return (Long)currentQuery.getSingleResult();


        }catch (Exception s)
        {
            s.printStackTrace();
            return 0;
        }

    }

    public boolean hasTransferBasedonClinicCode(String fileNumber,String clinicCode,Date appointmentDate)
    {
        try
        {
            String queryString = "select count(t.fileNumber) from Transfer t where t.fileNumber = :fileNumber and t.clinicCode = :code " +
                    " and t.appointment_Date_G = :date";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("code",clinicCode);
            currentQuery.setParameter("date",appointmentDate);

            long count = (Long)currentQuery.getSingleResult();

            if(count > 0) return true;else return false;



        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    public List<Transfer> getFutureTransfer(String fileNumber)
    {
        try
        {
            Date todayDate = new Date();
            Date endDate = AlfahresDateUtils.getEndOfDay(todayDate);

            String queryString = "select t from Transfer t where t.fileNumber = :fileNumber and t.appointment_Date_G > :end " +
                    " order by t.appointment_Date_G ASC ";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("end",endDate);
            currentQuery.setMaxResults(5);
            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Transfer>();
        }
    }

    public List<Transfer> getTransfers(String fileNumber)
    {
        try
        {
            Date todayDate = new Date();

            Date startDate = AlfahresDateUtils.getStartOfDay(todayDate);
            Date endDate = AlfahresDateUtils.getEndOfDay(todayDate);

            String queryString = "select t from Transfer t where t.fileNumber = :fileNumber and t.appointment_Date_G >= :start and " +
                    " t.appointment_Date_G <= :end order by t.appointment_Date_G ASC ";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("start",startDate);
            currentQuery.setParameter("end",endDate);
            currentQuery.setMaxResults(5);
            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Transfer>();
        }

    }
}
