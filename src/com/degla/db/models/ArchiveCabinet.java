package com.degla.db.models;

import java.util.Date;

/**
 * This cabinet represents an archive cabinet which will contain archived_files in the Enterprise's Archive room,
 * Each cabinet will be assigned to a given employee
 */

import javax.persistence.*;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name="TBL_ARCHIVECABINET")
@DiscriminatorValue("cabinet")
public class ArchiveCabinet extends EntityEO {
	/**
	 * this is the cabinet number identifier
	 */
    @Column(name="cabinetID")
	private String cabinetID;
	/**
	 * this is the location of the cabinet
	 */
    @Column(name="location")
	private String location;
	/**
	 * this is a simple description of the cabinet
	 */
    @Column(name="description")
	private String description;
	/**
	 * This is a timestamp showing when this cabinet has been created into the system.
	 */
    @Column(name="creationTime")
    @Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;
    @ManyToOne(cascade={REFRESH,MERGE,DETACH},fetch=FetchType.EAGER,targetEntity=Employee.class)
    @JoinColumn(name="EmpID")
	private Employee assignedTo;

	public void setCabinetID(String cabinetID) {
		this.cabinetID = cabinetID;
	}

	public String getCabinetID() {
		return this.cabinetID;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return this.location;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}


    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }
}