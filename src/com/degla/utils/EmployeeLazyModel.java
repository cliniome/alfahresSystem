package com.degla.utils;
import com.degla.dao.EmployeeDAO;
import com.degla.db.models.Employee;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by snouto on 08/05/2015.
 */
public class EmployeeLazyModel extends LazyDataModel<Employee> {

    private List<Employee> employees;
    private EmployeeDAO employeeDAO;


    public EmployeeLazyModel(EmployeeDAO dao)
    {
        this.employeeDAO = dao;
       /* this.initialLoad();*/
    }

   /* private void initialLoad()
    {
        employees = employeeDAO.getPaginatedResults(0,2);
        this.setRowCount((int) employeeDAO.getMaxResults());
        this.setWrappedData(employees);
        this.setPageSize(2);

    }*/

    @Override
    public List<Employee> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        this.employees = employeeDAO.getPaginatedResults(first,pageSize);

        if(getRowCount() <= 0){
            setRowCount((int) employeeDAO.getMaxResults());
        }

        // set the page dize
        setPageSize(pageSize);


        return employees;
    }

    @Override
    public Object getRowKey(Employee emp) {
        return emp.getId();
    }


    @Override
    public Employee getRowData(String empId) {

        Integer id = Integer.valueOf(empId);

        for (Employee emp : employees) {
            if(id.equals(emp.getId())){
                return emp;
            }
        }

        return null;
    }


}
