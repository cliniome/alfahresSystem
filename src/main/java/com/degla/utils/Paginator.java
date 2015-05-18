package com.degla.utils;

import java.util.List;

/**
 * Created by snouto on 08/05/2015.
 */
public interface Paginator<T> {

    public List<T> getPaginatedEntities(int first,int pageSize);
}
