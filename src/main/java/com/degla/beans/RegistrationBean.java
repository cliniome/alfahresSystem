package com.degla.beans;

import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.primefaces.component.commandbutton.CommandButton;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.List;

/**
 * Created by snouto on 08/05/2015.
 */
@ManagedBean(name="registrationBean")
@SessionScoped
public class RegistrationBean {

    public static String SHOWEMPLOYEE = "SHOWEMPLOYEE";

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean active;
    private String empID;
    //declaration of system service
    private SystemService systemService;
    private List<RoleEO> roles;
    private RoleEO chosenRole;
    private Employee updateableEmployee;




    @PostConstruct
    public void init()
    {
        try
        {
            systemService = SpringSystemBridge.services();

            this.addSomeRoles();
           this.setRoles(systemService.getRoleService().getRoles());



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onUpdate(ActionEvent event)
    {
        boolean result= systemService.getEmployeeService().updateEntity(this.getUpdateableEmployee());

        if(result)
            WebUtils.addMessage("Employee was updated Successfully");
        else WebUtils.addMessage("There was a problem updating the current employee");
    }

    public String onEdit()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        String empID = context.getExternalContext().getRequestParameterMap().get("empID").toString();

        if(empID != null)
        {
            Employee emp = systemService.getEmployeeService().getEntity(Integer.parseInt(empID));

            this.setUpdateableEmployee(emp);
        }

        return SHOWEMPLOYEE;
    }

    //TODO : Remove it after testing the whole system
    private void addSomeRoles()
    {
        if(systemService != null)
        {
            RoleEO[] roles = {
                    new RoleEO(RoleTypes.ADMIN.toString(),RoleTypes.ADMIN.toString(),"Admin Role"),
                    new RoleEO(RoleTypes.COORDINATOR.toString(),RoleTypes.COORDINATOR.toString(),"Coordinator Role"),
                    new RoleEO(RoleTypes.KEEPER.toString(),RoleTypes.KEEPER.toString() , "Files Keeper Role"),
                    new RoleEO(RoleTypes.RECEPTIONIST.toString(),RoleTypes.RECEPTIONIST.toString(),"Receptionist Role")
            };

            for(RoleEO role : roles)
            {
                systemService.getRoleService().addEntity(role);
            }
            System.out.println("Done. Adding Roles to the Database");

        }
    }


    public void onRegister(ActionEvent event)
    {
        Employee emp = new Employee();
        emp.setFirstName(this.getFirstName());
        emp.setLastName(this.getLastName());
        emp.setRole(this.chosenRole);
        emp.setPassword(this.getPassword());
        emp.setUserName(this.getUserName());
        emp.setActive(this.isActive());
        emp.setEmpID(this.getEmpID());

        //now adding the employee into the database
        boolean result = false;
        String message = "";
        if(emp.getId() == -1) {
            result = systemService.getEmployeeService().addEntity(emp);
            message = "Employee was added Successfully";
        }
        else {
            result = systemService.getEmployeeService().updateEntity(emp);
            message = "Employee was updated Successfully";
        }
        if(result)
            WebUtils.addMessage(message);

    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }




    public RoleEO getChosenRole() {
        return chosenRole;
    }

    public void setChosenRole(RoleEO chosenRole) {
        this.chosenRole = chosenRole;
    }

    public List<RoleEO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEO> roles) {
        this.roles = roles;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public Employee getUpdateableEmployee() {
        return updateableEmployee;
    }

    public void setUpdateableEmployee(Employee updateableEmployee) {
        this.updateableEmployee = updateableEmployee;
    }
}
