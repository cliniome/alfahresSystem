package com.degla.dao;

import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.utils.Paginator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */
@Component("employeeDAO")
public class EmployeeDAO extends AbstractDAO<Employee> {


    public List<Employee> getAllEmployees()
    {
        String queryString = "select e from Employee e";

        Query query = getManager().createQuery(queryString);
        return query.getResultList();
    }

    public List<Employee> getEmployeesByRole(String roleName,boolean active)
    {
        String queryString = "select e from Employee e where e.role.name=:rolename AND e.active=:active";
        Query query = getManager().createQuery(queryString);
        query.setParameter("rolename", roleName);
        query.setParameter("active",active);
        return query.getResultList();
    }

    public List<Employee> getEmployeesByRole(String roleName)
    {
        String queryString = "select e from Employee e where e.role.name=:rolename";
        Query query = getManager().createQuery(queryString);
        query.setParameter("rolename", roleName);
        return query.getResultList();
    }

    public boolean employeeIDExists(String empID)
    {
        try
        {
            String stringQuery = "select e from Employee e where e.empID = :id";
            Query query = getManager().createQuery(stringQuery);
            query.setParameter("id",empID);

            List<Employee> emps = query.getResultList();

            if(emps == null || emps.size() <=0)
                return false;
            else return true;

        }catch (Exception s)
        {
            s.printStackTrace();
            return false;
        }
    }

    public List<Employee> getEmployeesForClinicCode(String clinicCode)
    {
        try
        {
            String queryString = "select e from Employee e inner join e.clinics c where c.clinicCode = :code " +
                    " AND e.active=:active AND e.role.name =:roleName";
            Query currentQuery = getManager().createQuery(queryString);
            currentQuery.setParameter("code",clinicCode);
            currentQuery.setParameter("active",true);
            currentQuery.setParameter("roleName", RoleTypes.COORDINATOR.toString());

            return currentQuery.getResultList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }





    public Employee getEmployeeByUserName(String username)
    {
        try
        {
            String query = "select e from Employee e where e.userName=:username";
            Query currentQuery = getManager().createQuery(query);
            currentQuery.setParameter("username",username);

            return (Employee) currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public Integer getEmployeesCount()
    {
        try
        {
            String query = "select count(*) from Employee";
            Query currentQuery = getManager().createQuery(query);

            return (Integer)currentQuery.getSingleResult();

        }catch(Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    @Override
    public String getEntityName() {
        return "Employee";
    }


}
