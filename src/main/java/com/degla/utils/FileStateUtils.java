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
    "Transferred by coordinator","Missing"};

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


        if(readableState.toLowerCase().equals("Archived".toLowerCase()))
            state = FileStates.CHECKED_IN;
        else if (readableState.toLowerCase().equals("Checked Out".toLowerCase()))
            state = FileStates.CHECKED_OUT;
        else if (readableState.toLowerCase().equals("Received by Coordinator".toLowerCase()))
            state = FileStates.COORDINATOR_IN;
        else if (readableState.toLowerCase().equals("Sent out by Coordinator".toLowerCase()))
            state = FileStates.COORDINATOR_OUT;
        else if (readableState.toLowerCase().equals("Received by Clinic".toLowerCase()))
            state = FileStates.DISTRIBUTED;
        else if (readableState.toLowerCase().equals("Received by Keeper".toLowerCase()))
            state = FileStates.KEEPER_IN;
        else if (readableState.toLowerCase().equals("New File".toLowerCase()))
            state = FileStates.NEW;
        else if (readableState.toLowerCase().equals("Received by Receptionist".toLowerCase()))
            state = FileStates.RECEPTIONIST_IN;
        else if (readableState.toLowerCase().equals("Sent out by Receptionist".toLowerCase()))
            state = FileStates.RECEPTIONIST_OUT;
        else if (readableState.toLowerCase().equals("Prepared by Keeper".toLowerCase()))
            state = FileStates.OUT_OF_CABIN;
        else if (readableState.toLowerCase().equals("Transferred by coordinator".toLowerCase()))
            state = FileStates.TRANSFERRED;


        return state;
    }


    public String getReadableState()
    {
        String readableState = "Missing";

        switch (state)
        {
            case CHECKED_IN:
                return "Archived";
            case CHECKED_OUT:
                return "Checked Out";
            case COORDINATOR_IN:
                return "Received by Coordinator";
            case COORDINATOR_OUT:
                return "Sent out by Coordinator";
            case DISTRIBUTED:
                return "Received by Clinic";
            case KEEPER_IN:
                return "Received by Keeper";
            case NEW:
                return "New File";
            case RECEPTIONIST_IN:
                return "Received by Receptionist";
            case RECEPTIONIST_OUT:
                return "Sent out by Receptionist";
            case OUT_OF_CABIN:
                return "Prepared by Keeper";
            case TRANSFERRED:
                return "Transferred by coordinator";
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
