package com.degla.dao;

import com.degla.db.models.Clinic;
import org.springframework.stereotype.Component;


import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 05/06/15.
 */
@Component("clinicDAO")
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
}
