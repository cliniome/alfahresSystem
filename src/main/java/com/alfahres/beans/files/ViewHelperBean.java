package com.alfahres.beans.files;

import com.degla.db.models.FileStates;

import java.io.Serializable;

/**
 * Created by snouto on 12/08/15.
 */
public class ViewHelperBean implements Serializable {

    private String currentState;

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {

        if(currentState != null && !currentState.isEmpty())
            this.currentState = currentState;
    }
}
