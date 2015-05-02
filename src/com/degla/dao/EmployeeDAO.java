package com.degla.dao;

import com.degla.db.models.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by snouto on 02/05/2015.
 */
@Controller("employeeDAO")
public class EmployeeDAO extends AbstractDAO<Employee> {


    public List<Employee> getAllEmployees()
    {
        String queryString = "select e from Employee e";

        Query query = getManager().createQuery(queryString);
        return query.getResultList();
    }

}
