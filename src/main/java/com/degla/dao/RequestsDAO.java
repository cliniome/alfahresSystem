package com.degla.dao;

import com.degla.db.models.Request;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 15/05/15.
 */
@Component("requestsDAO")
public class RequestsDAO extends  AbstractDAO<Request> {

    @Override
    public String getEntityName() {
        return "Request";
    }
}
