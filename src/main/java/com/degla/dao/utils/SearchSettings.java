package com.degla.dao.utils;

import com.degla.db.models.FileStates;

import java.util.Date;

/**
 * Created by snouto on 09/10/15.
 */
public class SearchSettings {


    public static final int DISPLAY_ALL = 0;
    public static final int DISPLAY_BY_APPOINTMENT=1;

    private int type;
    private FileStates status = FileStates.MISSING;
    private Date appointmentDate;
    private boolean watchlist;
    private boolean inpatient;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FileStates getStatus() {
        return status;
    }

    public void setStatus(FileStates status) {
        this.status = status;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

    public boolean isInpatient() {
        return inpatient;
    }

    public void setInpatient(boolean inpatient) {
        this.inpatient = inpatient;
    }
}
