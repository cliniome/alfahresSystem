package com.degla.dao;

import com.degla.db.models.Clinic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 05/06/15.
 */
@Component("clinicDAO")
@Scope("prototype")
public class ClinicDAO extends AbstractDAO<Clinic> {
    @Override
    public String getEntityName() {
        return "Clinic";
    }



    public List<Clinic> getAllClinics()
    {
        String query = "select c from Clinic c ORDER BY c.clinicCode ASC";
        Query currentQuery = getManager().createQuery(query);
        return currentQuery.getResultList();

    }

    public List<Clinic> selectClinicByCodeOrName(String code)
    {
        try
        {
            String qry = "select c from Clinic c where c.clinicCode=:clinicCode OR c.clinicName LIKE :name";
            Query query = getManager().createQuery(qry);
            query.setParameter("clinicCode",code);
            query.setParameter("name","%"+code+"%");

            return query.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Clinic>();
        }
    }


    public boolean clinicExists(String clinicCode)
    {
        String qry = "select c from Clinic c where c.clinicCode=:clinicCode";
        Query query = getManager().createQuery(qry);
        query.setParameter("clinicCode",clinicCode);

        List objList = query.getResultList();

        if(objList == null || objList.size() <= 0 ) return false;

        else return true;
    }
}
