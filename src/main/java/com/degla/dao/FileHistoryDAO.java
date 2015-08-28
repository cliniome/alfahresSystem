package com.degla.dao;

import com.degla.db.models.FileHistory;
import com.degla.db.models.PatientFile;
import com.degla.db.models.Request;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 18/05/15.
 */
@Component("fileHistoryDAO")
@Scope("prototype")
public class FileHistoryDAO extends AbstractDAO<FileHistory> {
    @Override
    public String getEntityName() {
        return "fileHistoryDAO";
    }


    /**
     * This method will return all file history for the current active batch
     * @param file
     * @return A list of all workflow steps for the current file based on the latest batch number
     */
    public List<FileHistory> getFileHistory(PatientFile file)
    {
        try
        {
            /*
            select e from Message e
where e.msgFrom IN (select distinct m.msgFrom
                      from Message m
                      WHERE m.msgTo = ?
                      AND m.msgCheck = 0");
             */
            String queryString = "select h from FileHistory h where " +
                    "h.patientFile.fileID=:number and h.createdAt IN (select distinct (e.createdAt) from FileHistory  e where e.patientFile.fileID = :number) " +
                    "ORDER BY h.createdAt desc";

            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("number",file.getFileID());

            currentQuery.setMaxResults(30); // Set the maximum file history details to return

            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<FileHistory>();
        }
    }



    public Boolean appointmentExistsInHistory(Request request)
    {
        try
        {
            String queryString = "select count(distinct h.id) from FileHistory h where h.patientFile.fileID = :fileNumber and h.clinicCode=:code and h.appointment_Date_G = :appointmentDate";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",request.getFileNumber());
            currentQuery.setParameter("code",request.getClinicCode());
            currentQuery.setParameter("appointmentDate",request.getAppointment_Date());

            long resultCount = (Long)currentQuery.getSingleResult();

            if(resultCount > 0) return true;
            else return false;

        }catch (Exception s)
        {
            s.printStackTrace();

            return null;
        }
    }

}
