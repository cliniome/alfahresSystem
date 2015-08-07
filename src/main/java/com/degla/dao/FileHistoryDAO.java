package com.degla.dao;

import com.degla.db.models.FileHistory;
import com.degla.db.models.PatientFile;
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
            String queryString = "select h from FileHistory h where " +
                    "h.patientFile.fileID=:number ORDER BY h.createdAt desc";

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

}
