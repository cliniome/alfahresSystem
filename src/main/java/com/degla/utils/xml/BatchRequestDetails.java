package com.degla.utils.xml;

import java.io.Serializable;

/**
 * Created by snouto on 30/05/15.
 */
public class BatchRequestDetails implements Serializable {


    private String clinic_name;
    private String doc_name;
    private String cs_group_count;
    private String t_clinic_code;
    private String t_clinic_doc_code;

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getCs_group_count() {
        return cs_group_count;
    }

    public void setCs_group_count(String cs_group_count) {
        this.cs_group_count = cs_group_count;
    }

    public String getT_clinic_code() {
        return t_clinic_code;
    }

    public void setT_clinic_code(String t_clinic_code) {
        this.t_clinic_code = t_clinic_code;
    }

    public String getT_clinic_doc_code() {
        return t_clinic_doc_code;
    }

    public void setT_clinic_doc_code(String t_clinic_doc_code) {
        this.t_clinic_doc_code = t_clinic_doc_code;
    }
}
