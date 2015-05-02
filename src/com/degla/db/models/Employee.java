package com.degla.db.models;

public class Employee extends Actor {
	/**
	 * This is the employee ID within the enterprise
	 */
	protected String empID;
	public Role role;

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
}