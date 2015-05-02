package com.degla.db.models;

import org.hibernate.annotations.Index;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;

/**
 * This is the basic abstract class that represents any actor in the system.
 * 
 * It contains generic shared properties among different employees.
 */
@Entity
@Table(name="TBL_ACTOR")
@DiscriminatorValue("ACTOR")
public abstract class ActorEO extends EntityEO {
	/**
	 * This is the first name of the current actor
	 */
    @Column(name="firstName")
	protected String firstName;
	/**
	 * This is the last name of the employee
	 */
    @Column(name="lastName")
	protected String lastName;
	/**
	 * this is the username of the current actor
	 */
    @Index(name="userNameIndex")
    @Column(name="userName",unique = true,nullable = false)
	protected String userName;
	/**
	 * this is the password of the current employee.
	 */

    @Column(name="password",nullable = false)
	protected String password;


    @ManyToOne(cascade={REFRESH,MERGE,DETACH,PERSIST},fetch=FetchType.EAGER,targetEntity=RoleEO.class)
    @JoinColumn(name="ROLEID",nullable = false)
    protected RoleEO role;

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

    /**
     * Each Actor in the system has a role
     */
    public RoleEO getRole() {
        return role;
    }

    public void setRole(RoleEO role) {
        this.role = role;
    }
}