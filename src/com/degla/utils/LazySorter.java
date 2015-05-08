package com.degla.utils;

/**
 * Created by snouto on 08/05/2015.
 */

import java.util.Comparator;
import org.primefaces.model.SortOrder;

public class LazySorter<T extends AnnotatingModel> implements Comparator<T> {

    private String sortField;

    private SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }



    @Override
    public int compare(T first, T second) {
        try {

            Object value1 =  first.getClass().getField(this.sortField).get(first);
            Object value2 = second.getClass().getField(this.sortField).get(second);


            int value = ((Comparable)value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
