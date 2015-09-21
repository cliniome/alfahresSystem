package com.degla.utils;

import com.degla.db.models.Appointment;

import java.util.Comparator;

/**
 * Created by snouto on 10/08/15.
 */
public class LazyRequestSorter<T extends AnnotatingModel> implements Comparator<T> {

    @Override
    public int compare(T first, T second) {

        if((first instanceof Appointment) && (second instanceof Appointment))
        {
            Appointment firstReq = (Appointment)first;
            Appointment secondReq = (Appointment)second;
            BarcodeUtils firstUtils = new BarcodeUtils(firstReq.getFileNumber());
            BarcodeUtils secondUtils = new BarcodeUtils(secondReq.getFileNumber());
            int firstCabinId = Integer.parseInt(firstUtils.getCabinID()+firstUtils.getColumnNo());
            int secondCabinId = Integer.parseInt(secondUtils.getCabinID()+secondUtils.getColumnNo());

            if(firstCabinId > secondCabinId)
                return 1;
            else if (firstCabinId == secondCabinId) return 0;
            else return -1;

        }else return 0;
    }
}
