package com.degla.controllers;

import com.degla.db.models.Employee;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.RestfulRequest;

import java.util.List;

/**
 * Created by snouto on 18/05/15.
 */
public interface BasicRestfulOperations {


    public List<RestfulRequest> getNewRequests(String userName);
    public boolean updateFile(RestfulFile file,Employee emp);
    public boolean updateFiles(List<RestfulFile> files,Employee emp);

}
