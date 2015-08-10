package com.degla.dao;

import com.degla.db.models.ArchiveCabinet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 08/05/2015.
 */
@Component("archiveCabinetDAO")
@Scope("prototype")
public class ArchiveCabinetDAO extends AbstractDAO<ArchiveCabinet> {

    public List<ArchiveCabinet> getAllCabinets() throws Exception
    {
        String query = "select c from ArchiveCabinet c";
        Query currentQuery = getManager().createQuery(query);

        return currentQuery.getResultList();
    }

    public ArchiveCabinet getCabinetByID(String id)
    {
        try
        {
            String query = "select c from ArchiveCabinet c where c.cabinetID = :id";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("id",id);
            return (ArchiveCabinet) currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    @Override
    public String getEntityName() {
        return "ArchiveCabinet";
    }


}
