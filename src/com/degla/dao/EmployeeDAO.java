package com.degla.dao;

import com.degla.db.models.Employee;
import com.degla.utils.Paginator;
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
