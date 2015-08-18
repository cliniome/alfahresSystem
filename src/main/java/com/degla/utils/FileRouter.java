package com.degla.utils;

import com.degla.dao.EmployeeDAO;
import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by snouto on 17/05/15.
 */
@Component("fileRouter")
@Scope("prototype")
public class FileRouter {

    @Autowired
    private EmployeeDAO employeeDAO;



    public FileRouter(){}

    public FileRouter(EmployeeDAO dao)
    {
        this.employeeDAO = dao;
    }


    public void routeFiles(List<Request> requests , List<Employee> toEmployees) throws Exception
    {
        if(requests == null || requests.size() <= 0) throw new Exception("Requests should not be null or empty " +
                "during the distribution process");

        if(toEmployees == null || toEmployees.size() <=0) throw new Exception("Employees should not be null or empty " +
                "during the distribution process");

        int keepers_size = toEmployees.size();
        int requests_size = requests.size();

        Collections.sort(requests, new Comparator<Request>() {
            @Override
            public int compare(Request first, Request second) {

                try
                {
                    //get the size of the first file number
                    int firstSize = first.getFileNumber().length();
                    int secondSize = second.getFileNumber().length();

                    //get the last two Digits of the first File Number
                    String first_TwoDigits = first.getFileNumber().substring(firstSize-2);
                    String second_TwoDigits = second.getFileNumber().substring(secondSize-2);

                    //convert them into numbers
                    int firstNumber = Integer.parseInt(first_TwoDigits);
                    int secondNumber = Integer.parseInt(second_TwoDigits);

                    if(firstNumber > secondNumber) return 1;
                    else if (firstNumber < secondNumber) return -1;
                    else return 0;

                }catch (Exception s)
                {
                    return 0;
                }
            }
        });

        //Now route them according to the number of available employees
        int payload = requests_size/keepers_size;
        int offset = requests_size % keepers_size;

        //Loop over employees by payload magnitude
        for(int i=0;i<keepers_size;i++)
        {

            //loop over new Requests in step by step fashion
            for(int j=i*payload;j<(i+1)*payload;j++)
            {
                Request currentRequest = requests.get(j);
                Employee currentEmp = toEmployees.get(i);
                currentRequest.setAssignedTo(currentEmp);

            }
        }

        //take the requests mapped by offset
        List<Request> offset_requests = requests.subList(requests_size-offset,requests_size);

        for(Request request : offset_requests)
        {
            //pick a random employee
            int index = new Random().nextInt(keepers_size);

            Employee randomEmp = toEmployees.get(index);

            request.setAssignedTo(randomEmp);
        }

    }


    public void routeFiles(List<Request> newRequests) throws Exception
    {
        if(newRequests == null || newRequests.size() <=0) return;
        //get all employees whose Role is File keeper

        List<Employee> keepers = employeeDAO.getEmployeesByRole(RoleTypes.KEEPER.toString(),true);
        if(keepers == null || keepers.size() <= 0) return;

        //get their size
        int keepers_size = keepers.size();

        //get size of the new Requests
        int requests_size = newRequests.size();

        //Sort them depending upon their File Number

        Collections.sort(newRequests, new Comparator<Request>() {
            @Override
            public int compare(Request first, Request second) {

               try
               {
                   //get the size of the first file number
                   int firstSize = first.getFileNumber().length();
                   int secondSize = second.getFileNumber().length();

                   //get the last two Digits of the first File Number
                   String first_TwoDigits = first.getFileNumber().substring(firstSize-2);
                   String second_TwoDigits = second.getFileNumber().substring(secondSize-2);

                   if(first_TwoDigits.equals("le") || second_TwoDigits.equals("le"))
                   {
                       System.out.println("");
                   }

                   //convert them into numbers
                   int firstNumber = Integer.parseInt(first_TwoDigits);
                   int secondNumber = Integer.parseInt(second_TwoDigits);

                   if(firstNumber > secondNumber) return 1;
                   else if (firstNumber < secondNumber) return -1;
                   else return 0;

               }catch (Exception s)
               {
                   return 0;
               }


            }
        });

        //Now route them according to the number of available employees
        int payload = requests_size/keepers_size;
        int offset = requests_size % keepers_size;

        //Loop over employees by payload magnitude
       for(int i=0;i<keepers_size;i++)
       {

           //loop over new Requests in step by step fashion
           for(int j=i*payload;j<(i+1)*payload;j++)
           {
               Request currentRequest = newRequests.get(j);
               currentRequest.setAssignedTo(keepers.get(i));

           }
       }

        //take the requests mapped by offset
        List<Request> offset_requests = newRequests.subList(requests_size-offset,requests_size);

        for(Request request : offset_requests)
        {
            //pick a random employee
            int index = new Random().nextInt(keepers_size);

            Employee randomEmp = keepers.get(index);

            request.setAssignedTo(randomEmp);
        }





    }

}
