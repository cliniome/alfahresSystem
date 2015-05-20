package com.degla.utils;

/**
 * Created by snouto on 08/05/2015.
 */

import java.lang.reflect.Method;
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

            Method firstMethod = first.getClass().getMethod(getMethodName(sortField));
            Object value1 = firstMethod.invoke(first,null);
            //Object value1 =  first.getClass().getField(this.sortField).get(first);
            Method secondMethod = second.getClass().getMethod(getMethodName(sortField));
            Object value2 = secondMethod.invoke(second,null);

            //Object value2 = second.getClass().getField(this.sortField).get(second);

            int value = ((Comparable)value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }

    private String getMethodName(String sortField)
    {
        return "get"+capitalize(sortField);
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
