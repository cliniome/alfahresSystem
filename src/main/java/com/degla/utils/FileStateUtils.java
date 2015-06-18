package com.degla.utils;

import com.degla.db.models.FileStates;

import java.io.Serializable;

/**
 * Created by snouto on 19/06/15.
 */
public class FileStateUtils implements Serializable {

    private FileStates state;

    public FileStateUtils(FileStates state) {
        this.state = state;
    }
    //The default constructor
    public FileStateUtils(){}


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
            default:
                return readableState;
        }
    }

}
