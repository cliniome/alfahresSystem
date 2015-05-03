package com.degla.db.models;


import org.hibernate.annotations.Index;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="TBL_EMPLOYEE")
@DiscriminatorValue("employee")
public class Employee extends ActorEO implements UserDetails {
	/**
	 * This is the employee ID within the enterprise
	 */
    @Index(name="EmpIDIndex")
    @Column(name="EmpID",unique = true,nullable = false)
	protected String empID;


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
        return true;
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
}