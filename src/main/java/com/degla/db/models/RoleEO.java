package com.degla.db.models;

/**
 * Created by snouto on 02/05/2015.
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.springframework.security.core.GrantedAuthority;




@Entity(name="RoleEO")
@Table(name="TBL_ROLE")
@DiscriminatorValue("ROLE")
public class RoleEO extends EntityEO implements GrantedAuthority , Serializable {


    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_RECEPTIONIST = "ROLE_RECEPTIONIST";
    public static final String ROLE_KEEPER = "ROLE_KEEPER";
    public static final String ROLE_COORDINATOR = "ROLE_COORDINATOR";

    /**
     * The Role Name
     */
    @Index(name="RoleNameIndex")
    @Column(name="NAME")
    protected String name;
    /**
     * Simple Description about the role
     */
    @Column(name="DESCRIPTION",nullable = true)
    protected String description;


    @Column(name="DISPLAYNAME")
    protected String displayName;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String getAuthority() {

        return this.getName();
    }

    public RoleEO()
    {

    }

    public RoleEO(String name , String displayName , String description)
    {
        this.setName(name);
        this.setDisplayName(displayName);
        this.setDescription(description);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}