package com.degla.utils;

import com.degla.db.models.FileStates;
import com.degla.restful.models.FileModelStates;

import java.io.Serializable;

/**
 * Created by snouto on 19/06/15.
 */
public class FileStateUtils implements Serializable {

    private FileStates state;

    private String[] states = {"Archived","Checked Out","Received by Coordinator","Sent out by Coordinator",
    "Received by Clinic","Received by Keeper","New File","Received by Receptionist","Prepared by Keeper",
    "Transferred by coordinator","Missing","at Processing Coordinator","at Coding Coordinator","at Analysis Coordinator","at Incomplete Coordinator","Inpatient Submitted",
    "Temporary Stored"};

    public FileStateUtils(FileStates state) {
        this.state = state;
    }
    //The default constructor
    public FileStateUtils(){}


    public FileStates getState(String readableState)
    {
        FileStates state = FileStates.MISSING;

        if(readableState == null || readableState.length() <=0)
            return state;


        if(readableState.toLowerCase().equals("Stored in Cabin".toLowerCase()))
            state = FileStates.CHECKED_IN;
        else if (readableState.toLowerCase().equals("Checked Out".toLowerCase()))
            state = FileStates.CHECKED_OUT;
        else if (readableState.toLowerCase().equals("Received by Coordinator".toLowerCase()))
            state = FileStates.COORDINATOR_IN;
        else if (readableState.toLowerCase().equals("Sent out by Coordinator".toLowerCase()))
            state = FileStates.COORDINATOR_OUT;
        else if (readableState.toLowerCase().equals("Distributed To Clinics".toLowerCase()))
            state = FileStates.DISTRIBUTED;
        else if (readableState.toLowerCase().equals("Received by Keeper".toLowerCase()))
            state = FileStates.KEEPER_IN;
        else if (readableState.toLowerCase().equals("New File".toLowerCase()))
            state = FileStates.NEW;
        else if (readableState.toLowerCase().equals("Received by Receptionist".toLowerCase()))
            state = FileStates.RECEPTIONIST_IN;
        else if (readableState.toLowerCase().equals("Sent out by Receptionist".toLowerCase()))
            state = FileStates.RECEPTIONIST_OUT;
        else if (readableState.toLowerCase().equals("Prepared by Clerk".toLowerCase()))
            state = FileStates.OUT_OF_CABIN;
        else if (readableState.toLowerCase().equals("Transferred by coordinator".toLowerCase()))
            state = FileStates.TRANSFERRED;
        else if (readableState.toLowerCase().equals("at Processing Coordinator".toLowerCase()))
            state = FileStates.PROCESSING_COORDINATOR;
        else if (readableState.toLowerCase().equals("at Coding Coordinator".toLowerCase()))
            state = FileStates.CODING_COORDINATOR;
        else if (readableState.toLowerCase().equals("at Analysis Coordinator".toLowerCase()))
            state = FileStates.ANALYSIS_COORDINATOR;
        else if (readableState.toLowerCase().equals("at Incomplete Coordinator".toLowerCase()))
            state = FileStates.INCOMPLETE_COORDINATOR;
        else if (readableState.toLowerCase().equals("Inpatient Submitted".toLowerCase()))
            state = FileStates.INPATIENT_COMPLETED;
        else if (readableState.toLowerCase().equals("Temporary Stored".toLowerCase()))
            state = FileStates.TEMPORARY_STORED;


        return state;
    }


    public String getReadableState()
    {
        String readableState = "Missing";

        switch (state)
        {
            case CHECKED_IN:
                return "Stored in Cabin";
            case CHECKED_OUT:
                return "Checked Out";
            case COORDINATOR_IN:
                return "Received by Coordinator";
            case COORDINATOR_OUT:
                return "Sent out by Coordinator";
            case DISTRIBUTED:
                return "Distributed To Clinics";
            case KEEPER_IN:
                return "Received by Keeper";
            case NEW:
                return "New File";
            case RECEPTIONIST_IN:
                return "Received by Receptionist";
            case RECEPTIONIST_OUT:
                return "Sent out by Receptionist";
            case OUT_OF_CABIN:
                return "Prepared by Clerk";
            case TRANSFERRED:
                return "Transferred by coordinator";
            case PROCESSING_COORDINATOR:
                return "at Processing Coordinator";
            case ANALYSIS_COORDINATOR:
                return "at Analysis Coordinator";
            case CODING_COORDINATOR:
                return "at Coding Coordinator";
            case INCOMPLETE_COORDINATOR:
                return "at Incomplete Coordinator";
            case INPATIENT_COMPLETED:
                return "Inpatient Submitted";
            case TEMPORARY_STORED:
                return "Temporary Stored";
            default:
                return readableState;
        }
    }

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }
}
