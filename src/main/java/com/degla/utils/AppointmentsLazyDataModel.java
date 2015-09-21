package com.degla.utils;

import com.degla.dao.AbstractDAO;
import com.degla.dao.AppointmentsDAO;
import com.degla.dao.RequestsDAO;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by snouto on 12/09/15.
 */
public class AppointmentsLazyDataModel<T extends AnnotatingModel> extends LazyDataModel<T> {

    private List<T> dataModels = new ArrayList<T>();
    private AbstractDAO<T> paginator;
    private boolean watchList;

    public boolean isWatchList() {
        return watchList;
    }

    public void setWatchList(boolean watchList) {
        this.watchList = watchList;
    }


    public AppointmentsLazyDataModel(AbstractDAO<T> paginatorModel)
    {
        this.paginator = paginatorModel;
    }

    public AppointmentsLazyDataModel(AbstractDAO<T> paginatorModel, boolean watchList)
    {
        this.paginator = paginatorModel;
        this.watchList = watchList;
    }


    public boolean addModel(T model)
    {
        if(dataModels == null) dataModels = new ArrayList<T>();

        return dataModels.add(model);
    }




    @Override
    public T getRowData(String rowKey) {
        for(T model : dataModels) {
            if(model.getRowKey().equals(rowKey))
                return model;
        }

        return null;
    }

    @Override
    public Object getRowKey(T model) {
        return model.getRowKey();
    }

    @Override
    public void setRowIndex(int rowIndex) {

        if(rowIndex == -1 || getPageSize() == 0)
        {

        }else  super.setRowIndex(rowIndex % getPageSize());

    }


    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try
        {

            List<T> data = new ArrayList<T>();
            //select the data

            if(watchList)
            {
                this.dataModels = (List<T>) ((AppointmentsDAO)paginator).getAllWatchListRequests(first,pageSize);
                this.setRowCount((int) ((AppointmentsDAO)paginator).getCountOfWatchListRequests());
            }else
            {
                this.dataModels = paginator.getPaginatedResults(first, pageSize);
                this.setRowCount((int) paginator.getMaxResults());
            }
            //filter
            for(T model : dataModels) {
                boolean match = true;

                for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    try {
                        String filterProperty = it.next();
                        String filterValue = filters.get(filterProperty).toString();
                        String methodName = "get"+Capitalize(filterProperty);
                        String fieldValue = String.valueOf(model.getClass().getMethod(methodName).invoke(model));

                        if(filterProperty.equals("appointment_Date"))
                        {
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
                            Date passedValue = formatter.parse(filterValue);
                            formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date object_Date = formatter.parse(fieldValue);

                            if(DateUtils.isSameDay(passedValue,object_Date))
                            {
                                match = true;
                            }else
                            {
                                match = false;
                                break;
                            }

                        }else
                        {
                            if(filterValue == null || fieldValue.startsWith(filterValue)) {
                                match = true;
                            }
                            else {
                                match = false;
                                break;
                            }
                        }


                    } catch(Exception e) {
                        match = false;
                    }
                }

                if(match) {
                    data.add(model);
                }
            }

            //Now try to sort them
            Collections.sort(data, new LazyRequestSorter());

            //sort
            if(sortField != null) {
                Collections.sort(data, new LazySorter(sortField, sortOrder));
            }

            //rowCount
            int dataSize = data.size();
            //this.setRowCount(dataSize);





            //paginate
            if(dataSize > pageSize) {
                try {
                    return data.subList(first, first + pageSize);
                }
                catch(IndexOutOfBoundsException e) {
                    return data.subList(first, first + (dataSize % pageSize));
                }
            }
            else {


                return data;
            }




        }catch(Exception s)
        {


            return null;
        }
    }

    public static String Capitalize(String line)
    {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
