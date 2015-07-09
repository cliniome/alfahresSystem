package com.degla.beans;


import com.degla.db.models.Clinic;
import com.degla.db.models.Employee;
import com.degla.db.models.RoleEO;
import com.degla.db.models.RoleTypes;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snouto on 08/05/2015.
 */
@ManagedBean(name="registrationBean")
@ViewScoped
public class RegistrationBean {

    public static String SHOWEMPLOYEE = "UPDATEEMPLOYEENOW";
    public static String CANCEL_SHOWEMPLOYEES = "CANCELANDSHOWEMPS";
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
    private List<Clinic> clinics;
    private List<Clinic> filteredClinics;
    private List<Clinic> chosenClinics;
    private boolean coordinator;
    private String passedEmpId;
    private String filterQuery;


    @PostConstruct
    public void init()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            this.setClinics(systemService.getClinicManager().getAllClinics());
           // this.addSomeRoles();
           this.setRoles(systemService.getRoleService().getRoles());

            this.setFilteredClinics(new ArrayList<Clinic>());
            this.setChosenClinics(new ArrayList<Clinic>());

            Object empID = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

            if(empID != null)
                this.setPassedEmpId(empID.toString());



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }



    public void onFilter(ActionEvent event)
    {
        try
        {
            if(this.getFilterQuery() == null || this.getFilterQuery().length() <=0)
                return;

            List<Clinic> filteredClinics = systemService.getClinicManager()
                    .selectClinicByCodeOrName(getFilterQuery());

            if(filteredClinics == null)
                filteredClinics = new ArrayList<Clinic>();

            this.setClinics(filteredClinics);


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public void onReset(ActionEvent event)
    {
        try
        {
            this.setClinics(systemService.getClinicManager().getAllClinics());

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public boolean returnUpdateableCoordinator()
    {
        if(this.getUpdateableEmployee() != null)
            return this.getUpdateableEmployee().getRole().getName().equalsIgnoreCase(RoleTypes.COORDINATOR.toString());
        else return false;
    }


    public void onRoleChanged()
    {
        RoleEO selectedRole = this.getChosenRole();

        if(selectedRole != null)
        {
            if(selectedRole.getName().equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))
                this.setCoordinator(true);
            else this.setCoordinator(false);
        }
    }


    public void selectClinicFromDialog(Clinic clinic)
    {
        RequestContext.getCurrentInstance().closeDialog(clinic);



    }


    public void openSelectClinicDialog()
    {
       try
       {
           Map<String,Object> props = new HashMap<String, Object>();

           props.put("modal",true);

           RequestContext.getCurrentInstance().openDialog("selectClinic",props,null);


       }catch (Exception s)
       {
           s.printStackTrace();
       }
    }

    public String onCancel()
    {
        try
        {

            this.clear();
        }catch (Exception s)
        {
            s.printStackTrace();
        }
        finally {

            return CANCEL_SHOWEMPLOYEES;
        }
    }

    private void clear() {

        this.setChosenClinics(new ArrayList<Clinic>());
        this.setChosenRole(null);
        this.setCoordinator(false);
    }


    public void onClinicChosen(SelectEvent event)
    {
        //get the clinic
        Clinic selectedClinic = (Clinic)event.getObject();

        if(selectedClinic != null)
            this.getChosenClinics().add(selectedClinic);

        this.setFilteredClinics(new ArrayList<Clinic>());
    }

    public void onUpdateableEmployeeClinicChosen(SelectEvent event)
    {
        //get the clinic
        Clinic selectedClinic = (Clinic)event.getObject();

        if(selectedClinic != null)
        {
            if(this.getUpdateableEmployee().getClinics() == null)
                this.getUpdateableEmployee().setClinics(new ArrayList<Clinic>());

            this.getUpdateableEmployee().getClinics().add(selectedClinic);
        }

    }

    public void onDeleteClinic(Clinic clinic)
    {
        try
        {
            if(clinic != null && this.getChosenClinics() != null && this.getChosenClinics().size() > 0)
            {
                this.getChosenClinics().remove(clinic);
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public void onDeleteUpdateableEmployeeClinic(Clinic clinic)
    {
        try
        {
            if(clinic != null && this.getUpdateableEmployee() != null &&
                    this.getUpdateableEmployee().getClinics() != null)
            {
                this.getUpdateableEmployee().getClinics().remove(clinic);
            }

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
        emp.setClinics(this.getChosenClinics());

        //now adding the employee into the database
        boolean result = false;
        String message = "";
        if(emp.getId() == -1) {
            result = systemService.getEmployeeService().addEntity(emp);
            message = "Employee was added Successfully";
            this.setChosenClinics(new ArrayList<Clinic>());
            this.clear();
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

        if(updateableEmployee == null)
            updateableEmployee = systemService.getEmployeeService().getEntity(Integer.parseInt(getPassedEmpId()));

        return updateableEmployee;
    }

    public void setUpdateableEmployee(Employee updateableEmployee) {
        this.updateableEmployee = updateableEmployee;
    }

    public List<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<Clinic> clinics) {
        this.clinics = clinics;
    }

    public List<Clinic> getFilteredClinics() {
        return filteredClinics;
    }

    public void setFilteredClinics(List<Clinic> filteredClinics) {
        this.filteredClinics = filteredClinics;
    }

    public List<Clinic> getChosenClinics() {
        return chosenClinics;
    }

    public void setChosenClinics(List<Clinic> chosenClinics) {
        this.chosenClinics = chosenClinics;
    }

    public boolean isCoordinator() {
        return coordinator;
    }

    public void setCoordinator(boolean coordinator) {
        this.coordinator = coordinator;
    }

    public String getPassedEmpId() {
        return passedEmpId;
    }

    public void setPassedEmpId(String passedEmpId) {
        this.passedEmpId = passedEmpId;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }
}
