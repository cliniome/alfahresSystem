package com.degla.beans;

import com.degla.db.models.Request;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by snouto on 20/05/15.
 */
@ManagedBean(name="searchRequestsBean")
@ViewScoped
public class SearchRequestsBean {

    private String searchQuery;

    private SystemService systemService;

    private List<Request> searchedRequests;



    @PostConstruct
    public void init()
    {
        try
        {
            systemService = SpringSystemBridge.services();

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public boolean foundResults()
    {
        if(searchedRequests == null || searchedRequests.size() <=0) return false;
        else return true;
    }

    public int totalCount()
    {
        if(foundResults()) return searchedRequests.size();
        else return 0;
    }

    public void doSearch()
    {
        if(getSearchQuery() != null && getSearchQuery().length() > 0)
        {
            searchedRequests = systemService.getRequestsManager().searchRequests(getSearchQuery());

            if(searchedRequests == null || searchedRequests.size() <=0)
                WebUtils.addMessage("There are no available requests");

        }else
            WebUtils.addMessage("You have to supply a search Query");
    }


    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Request> getSearchedRequests() {
        return searchedRequests;
    }

    public void setSearchedRequests(List<Request> searchedRequests) {
        this.searchedRequests = searchedRequests;
    }
}
