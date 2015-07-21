package com.degla.beans.requests;

import com.degla.db.models.Employee;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 21/07/15.
 */
@ManagedBean(name="redistributeRequestsBean")
@SessionScoped
public class RedistributeRequestsBean implements Serializable {


    private SystemService systemService;
    private Employee fromKeeper;
    private DualListModel<Employee> toKeepersList;
    private List<Employee> picker_toEmployees;
    private List<Employee> picker_fromEmployees;

    @PostConstruct
    public void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            this.setPicker_fromEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString()));
            this.setPicker_toEmployees(new ArrayList<Employee>());
            this.setToKeepersList(new DualListModel<Employee>(getPicker_fromEmployees(), getPicker_toEmployees()));

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @PreDestroy
    public void onClear()
    {
        this.setPicker_toEmployees(new ArrayList<Employee>());
        this.setToKeepersList(new DualListModel<Employee>(getPicker_fromEmployees(), getPicker_toEmployees()));
    }

    public void redistributeRequests(ActionEvent event)
    {
        try
        {
            if(this.fromKeeper == null)
            {
                WebUtils.addMessage("Please Choose a Keeper to select his/her Requests First before proceeding.");
                return;
            }

            if(this.getToKeepersList().getTarget() == null || this.getToKeepersList().getTarget().size() <=0)
            {
                WebUtils.addMessage("Please choose Keeper(s) to redistribute requests to them first before " +
                        "proceeding");
                return;
            }

            List<Request> availableRequests = systemService.getRequestsManager().getNewRequestsFor(this.fromKeeper.getUsername());

            if(availableRequests == null || availableRequests.size() <=0)
            {
                WebUtils.addMessage("There are no Assigned Requests for the selected keeper " +
                        ", Please Try another Employee.");
                return;
            }

            List<Employee> toKeepers = this.getToKeepersList().getTarget();

            //Now route those requests
            systemService.getFileRouter().routeFiles(availableRequests,(List<Employee>)toKeepers);

            //then update those requests
            boolean updateResult = true;

            for(Request currentRequest : availableRequests)
            {
                //update them respectively
               updateResult &= systemService.getRequestsManager().updateEntity(currentRequest);
            }

            if(updateResult)
            {
                WebUtils.addMessage(String.format("Re-Distributing Requests for Employee %s was Successful",this.fromKeeper.getfullName()));

            }

            //Clear everything
            this.onClear();


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onChange()
    {
        try
        {
            if(this.fromKeeper != null)
            {
                //set the from Employees to all active keepers except fromKeeper
                List<Employee> activeKeepers = systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true);

                List<Employee> filteredEmps = new ArrayList<Employee>();

                if(activeKeepers != null && activeKeepers.size() > 0)
                {
                    for(Employee emp : activeKeepers)
                    {
                        if(emp.getId() != this.fromKeeper.getId())
                            filteredEmps.add(emp);
                    }
                }

                this.setPicker_fromEmployees(filteredEmps);
                this.toKeepersList = new DualListModel<Employee>(this.getPicker_fromEmployees(), this.getPicker_toEmployees());
            }else
            {
                this.setPicker_fromEmployees(systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true));
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public List<Employee> getKeepers()
    {
        try
        {
            return systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString());

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    public Employee getFromKeeper() {
        return fromKeeper;
    }

    public void setFromKeeper(Employee fromKeeper) {
        this.fromKeeper = fromKeeper;
    }

    public DualListModel<Employee> getToKeepersList() {
        return toKeepersList;
    }

    public void setToKeepersList(DualListModel<Employee> toKeepersList) {
        this.toKeepersList = toKeepersList;
    }

    public List<Employee> getPicker_toEmployees() {
        return picker_toEmployees;
    }

    public void setPicker_toEmployees(List<Employee> picker_toEmployees) {
        this.picker_toEmployees = picker_toEmployees;
    }

    public List<Employee> getPicker_fromEmployees() {
        return picker_fromEmployees;
    }

    public void setPicker_fromEmployees(List<Employee> picker_fromEmployees) {
        this.picker_fromEmployees = picker_fromEmployees;
    }
}
