package com.degla.dao;

import com.degla.db.models.Transfer;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by snouto on 20/06/15.
 */
@Component("transferDAO")
public class TransferDAO extends AbstractDAO<Transfer> {
    @Override
    public String getEntityName() {
        return "Transfer";
    }




    public boolean hasTransfer(String fileNumber)
    {
        try
        {
            List<Transfer> transfers = this.getTransfers(fileNumber);

            if(transfers == null || transfers.size() <=0) return false;
            else return true;

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
            String queryString = "select t from Transfer t where t.fileNumber = :fileNumber";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("fileNumber",fileNumber);
            return currentQuery.getResultList();


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Transfer>();
        }

    }
}
