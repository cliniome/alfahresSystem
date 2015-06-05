package com.degla.db.models;

import com.degla.utils.AnnotatingModel;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by snouto on 05/06/15.
 */
@Entity(name="Clinic")
@Table(name="TBL_CLINICS")
public class Clinic implements AnnotatingModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="ClinicName",nullable = false)
    private String clinicName;
    @Index(name="ClinicCodeIdx")
    @Column(name="ClinicCode",nullable = false)
    private String clinicCode;

    public Clinic(){}

    public Clinic(String clinicName , String clinicCode)
    {
        this.setClinicName(clinicName);
        this.setClinicCode(clinicCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    @Override
    public Object getRowKey() {

        return this.getId();
    }
}
