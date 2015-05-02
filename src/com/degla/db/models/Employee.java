package com.degla.db.models;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TBL_EMPLOYEE")
@DiscriminatorValue("employee")
public class Employee extends ActorEO {
	/**
	 * This is the employee ID within the enterprise
	 */
    @Column(name="EmpID")
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
}