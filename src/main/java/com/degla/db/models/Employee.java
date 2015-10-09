package com.degla.db.models;


import org.hibernate.annotations.Index;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static javax.persistence.CascadeType.*;
@Entity(name="Employee")
@Table(name="TBL_EMPLOYEE")
@DiscriminatorValue("employee")
public class Employee extends ActorEO implements UserDetails {
	/**
	 * This is the employee ID within the enterprise
	 */
    @Index(name="EmpIDIndex")
    @Column(name="EmpID",unique = true,nullable = false)
	protected String empID;

    @Column(name="active")
    private boolean active = false;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {MERGE,REFRESH,DETACH,REMOVE})
    @JoinTable(name="TBL_EMP_CLINICS",joinColumns = {
             @JoinColumn(name="emp_id",referencedColumnName ="id")
    },inverseJoinColumns = {

            @JoinColumn(name = "clinic_id",referencedColumnName = "id")
    })
    private List<Clinic> clinics;


    @Column(name = "deleted",nullable = true)
    private boolean deleted = false;


    public Employee(){

        clinics = new ArrayList<Clinic>();
    }


    @Override
    public String toString() {

        return this.getfullName();
    }

    /**
	 * This method will return a MD5 hash string of the current employeeID+UserName or UserName only to act as a primary key for that object and foreign key identifier in associated models.
	 */
	public String getIdentifier() {
		throw new UnsupportedOperationException();
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getEmpID() {
		return this.empID;
	}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(this.getRole());
        return authorities;
    }


    


    public String getfullName()
    {
        return String.format("%s %s (%s)",this.getFirstName(),this.getLastName(),this.getUserName());
    }

    @Override
    public String getUsername() {
        return this.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //TODO : Change this if you managed to add isactive property to the Employee
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<Clinic> clinics) {
        this.clinics = clinics;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}