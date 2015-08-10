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
            long transfersCountByDate = this.getTransfersCountByDate(fileNumber,date);

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
            String queryString = "select count(t.fileNumber) from Transfer t where t.fileNumber = :fileNumber";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            return (Long)currentQuery.getSingleResult();


        }catch (Exception s)
        {
            s.printStackTrace();
            return 0;
        }

    }

    public boolean hasTransferBasedonClinicCode(String fileNumber,String clinicCode,String hijri,String time)
    {
        try
        {
            String queryString = "select count(t) from Transfer t where t.fileNumber = :fileNumber and t.clinicCode = :code " +
                    " and t.appointment_Hijri_Date =:hijriDate and t.appointmentTime = :time";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setParameter("code",clinicCode);
            currentQuery.setParameter("hijriDate",hijri);
            currentQuery.setParameter("time",time);

            long count = (Long)currentQuery.getSingleResult();

            if(count > 0) return true;else return false;



        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    public List<Transfer> getTransfers(String fileNumber)
    {
        try
        {
            String queryString = "select t from Transfer t where t.fileNumber = :fileNumber order by t.appointment_Date_G ASC ";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            currentQuery.setMaxResults(5);
            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Transfer>();
        }

    }
}
