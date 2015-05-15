package com.degla.dao;

import com.degla.db.models.Request;

/**
 * Created by snouto on 15/05/15.
 */
public class RequestsDAO extends  AbstractDAO<Request> {

    @Override
    public String getEntityName() {
        return "Request";
    }
}
