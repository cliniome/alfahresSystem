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


    public static List<FileStates> getAllowedStatesFor(Employee emp)
    {
        List<FileStates> allowedStates = new ArrayList<FileStates>();

        String role = emp.getRole().getName();

        RoleTypes type = RoleTypes.valueOf(role);

        switch (type)
        {
            case ANALYSIS_COORDINATOR:
            {
                allowedStates.add(FileStates.ANALYSIS_COORDINATOR);
                allowedStates.add(FileStates.INPATIENT_COMPLETED);
                allowedStates.add(FileStates.TEMPORARY_STORED);
            }
            break;

            case CODING_COORDINATOR:
            {
                allowedStates.add(FileStates.ANALYSIS_COORDINATOR);
                allowedStates.add(FileStates.INPATIENT_COMPLETED);
                allowedStates.add(FileStates.TEMPORARY_STORED);
            }
                break;
            case COORDINATOR:
            {
                allowedStates.add(FileStates.COORDINATOR_IN);
                allowedStates.add(FileStates.COORDINATOR_OUT);
                allowedStates.add(FileStates.DISTRIBUTED);
                allowedStates.add(FileStates.MISSING);
                allowedStates.add(FileStates.TRANSFERRED);
            }
                break;
            case INCOMPLETE_COORDINATOR:
            {
                allowedStates.add(FileStates.ANALYSIS_COORDINATOR);
                allowedStates.add(FileStates.INPATIENT_COMPLETED);
                allowedStates.add(FileStates.TEMPORARY_STORED);
            }
                break;
            case KEEPER:
            {
                allowedStates.add(FileStates.MISSING);
                allowedStates.add(FileStates.OUT_OF_CABIN);
                allowedStates.add(FileStates.CHECKED_OUT);
                allowedStates.add(FileStates.CHECKED_IN);

            }
                break;
            case PROCESSING_COORDINATOR:
            {
                allowedStates.add(FileStates.ANALYSIS_COORDINATOR);
                allowedStates.add(FileStates.INPATIENT_COMPLETED);
                allowedStates.add(FileStates.TEMPORARY_STORED);
            }
                break;
            case RECEPTIONIST:
            {
                allowedStates.add(FileStates.RECEPTIONIST_IN);
                allowedStates.add(FileStates.RECEPTIONIST_OUT);
                allowedStates.add(FileStates.MISSING);
            }
                break;
        }


        return allowedStates;
    }

    public static List<FileStates> getScannableStates(Employee emp)
    {
        List<FileStates> states = new ArrayList<FileStates>();

        String role = emp.getRole().getName();

        if(role.equalsIgnoreCase(RoleTypes.KEEPER.toString()))
        {
            states.add(FileStates.RECEPTIONIST_IN);
            //states.add(FileStates.COORDINATOR_OUT);
        }
        else if (role.equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
        {
            //states.add(FileStates.CHECKED_OUT);
            states.add(FileStates.COORDINATOR_OUT);
            states.add(FileStates.INPATIENT_COMPLETED);

        }
        else if (role.equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))
        {
            states.add(FileStates.RECEPTIONIST_OUT);
            states.add(FileStates.CHECKED_OUT);
            states.add(FileStates.TRANSFERRED);

        }else if (role.equalsIgnoreCase(RoleTypes.ANALYSIS_COORDINATOR.toString()) ||
                role.equalsIgnoreCase(RoleTypes.CODING_COORDINATOR.toString()) ||
                role.equalsIgnoreCase(RoleTypes.INCOMPLETE_COORDINATOR.toString()))
        {
            states.add(FileStates.ANALYSIS_COORDINATOR);
            states.add(FileStates.CODING_COORDINATOR);
            states.add(FileStates.INCOMPLETE_COORDINATOR);
            states.add(FileStates.PROCESSING_COORDINATOR);
            states.add(FileStates.CHECKED_IN);
            states.add(FileStates.TEMPORARY_STORED);

        }else if (role.equalsIgnoreCase(RoleTypes.PROCESSING_COORDINATOR.toString()))
        {
            states.add(FileStates.ANALYSIS_COORDINATOR);
            states.add(FileStates.CODING_COORDINATOR);
            states.add(FileStates.INCOMPLETE_COORDINATOR);
            states.add(FileStates.PROCESSING_COORDINATOR);
            states.add(FileStates.CHECKED_IN);
            states.add(FileStates.TEMPORARY_STORED);
            states.add(FileStates.CHECKED_OUT);


        }

        return states;
    }
}
