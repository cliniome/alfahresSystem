package com.degla.restful.utils;

import com.degla.system.SystemService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by snouto on 09/08/15.
 */
public class AlfahresDateUtils {

    public static Date getEndOfDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return calendar.getTime();
    }

    public static Date addOneDay(Date date)
    {
        Calendar calc = Calendar.getInstance();
        calc.setTime(date);
        calc.add(Calendar.DAY_OF_MONTH,1);
        return calc.getTime();
    }

    public static Date getStartOfDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTime();
    }


    public static boolean DatesInTheSameDay(Date first , Date second)
    {
        //get end of both days
        Date end_first = getEndOfDay(first);
        Date end_second = getEndOfDay(second);

        return (end_first.compareTo(end_second) == 0);
    }

}
