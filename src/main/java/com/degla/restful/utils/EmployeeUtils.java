package com.degla.restful.utils;

import com.degla.db.models.Employee;
import com.degla.db.models.FileStates;
import com.degla.db.models.RoleTypes;
import com.degla.restful.models.FileModelStates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 30/05/15.
 */
public class EmployeeUtils {


    public static final int RECEIVE = 0;
    public static final int SEND_OUT = 1;


    public static List<FileStates> getScannableStates(Employee emp)
    {
        List<FileStates> states = new ArrayList<FileStates>();

        String role = emp.getRole().getName();

        if(role.equalsIgnoreCase(RoleTypes.KEEPER.toString()))
        {
            states.add(FileStates.RECEPTIONIST_IN);
            states.add(FileStates.COORDINATOR_OUT);
        }
        else if (role.equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
        {
            states.add(FileStates.CHECKED_OUT);
            states.add(FileStates.COORDINATOR_OUT);

        }
        else if (role.equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))
        {
            states.add(FileStates.RECEPTIONIST_OUT);
            states.add(FileStates.CHECKED_OUT);
        }

        return states;
    }
}
