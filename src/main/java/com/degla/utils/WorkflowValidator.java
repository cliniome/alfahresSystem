package com.degla.utils;

import com.degla.db.models.FileStates;

/**
 * Created by snouto on 20/05/15.
 */
public class WorkflowValidator  {


    public static boolean canProceed(FileStates currentState , FileStates nextState)
    {
        if(nextState == FileStates.CHECKED_OUT && currentState == FileStates.CHECKED_IN) return true;
        else if(nextState.getStep() == (currentState.getStep()+1)) return true;
        else if (nextState == FileStates.MISSING) return true;
        else return false;
    }
}
