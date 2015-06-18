package com.degla.dao;

import com.degla.db.models.RoleEO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 08/05/2015.
 */
@Component("roleDAO")
@Scope("prototype")
public class RoleDAO extends  AbstractDAO<RoleEO> {

    public List<RoleEO> getRoles()
    {
        String query = "select r from RoleEO r";
        Query currentQuery = getManager().createQuery(query);
        return currentQuery.getResultList();
    }

    public RoleEO getRoleByDisplayName(String displayName)
    {
        String query = "select r from RoleEO r where r.displayName=:display";
        Query currentQuery = getManager().createQuery(query);
        currentQuery.setParameter("display",displayName);

        return (RoleEO) currentQuery.getSingleResult();
    }

    @Override
    public String getEntityName() {
        return "RoleEO";
    }


}
