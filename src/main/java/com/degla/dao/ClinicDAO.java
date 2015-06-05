package com.degla.dao;

import com.degla.db.models.Clinic;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 05/06/15.
 */
@Component("clinicDAO")
public class ClinicDAO extends AbstractDAO<Clinic> {
    @Override
    public String getEntityName() {
        return "Clinic";
    }
}
