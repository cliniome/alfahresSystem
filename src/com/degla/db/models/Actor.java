package com.degla.db.models;

/**
 * This is the basic abstract class that represents any actor in the system.
 * 
 * It contains generic shared properties among different employees.
 */
public abstract class Actor {
	/**
	 * This is the first name of the current actor
	 */
	protected String firstName;
	/**
	 * This is the last name of the employee
	 */
	protected String lastName;
	/**
	 * this is the username of the current actor
	 */
	protected String userName;
	/**
	 * this is the password of the current employee.
	 */
	protected String password;

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
}