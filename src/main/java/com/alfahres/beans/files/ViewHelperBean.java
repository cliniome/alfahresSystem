package com.alfahres.beans.files;

import com.degla.db.models.FileStates;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 12/08/15.
 */
public class ViewHelperBean implements Serializable {

    private String currentState;

    private Date appointmentDate;

    private String fileNumber;

    private boolean inWatchList;

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {

        if(currentState != null && !currentState.isEmpty())
            this.currentState = currentState;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public boolean isInWatchList() {
        return inWatchList;
    }

    public void setInWatchList(boolean inWatchList) {
        this.inWatchList = inWatchList;
    }
}
