package com.alfahres.beans.files;

import com.degla.dao.utils.SearchSettings;
import com.degla.db.models.FileStates;
import com.degla.db.models.PatientFile;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 12/08/15.
 */
public class ViewHelperBean implements Serializable {

    private String currentState = FileStates.MISSING.toString();

    private String secondState = FileStates.MISSING.toString();

    private Date appointmentDate;

    private String fileNumber;

    private PatientFile tempFile;

    private boolean inWatchList;

    private boolean inpatient;


    private SearchSettings printSearchSettings;


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

    public PatientFile getTempFile() {
        return tempFile;
    }

    public void setTempFile(PatientFile tempFile) {
        this.tempFile = tempFile;
    }

    public SearchSettings getPrintSearchSettings() {
        return printSearchSettings;
    }

    public void setPrintSearchSettings(SearchSettings printSearchSettings) {
        this.printSearchSettings = printSearchSettings;
    }

    public String getSecondState() {
        return secondState;
    }

    public void setSecondState(String secondState) {
        this.secondState = secondState;
    }

    public boolean isInpatient() {
        return inpatient;
    }

    public void setInpatient(boolean inpatient) {
        this.inpatient = inpatient;
    }
}
