package com.degla.utils.xml;

import com.degla.beans.files.FileUploadWizardBean;
import com.degla.db.models.Request;
import com.degla.exceptions.RequestException;
import com.degla.system.SystemService;
import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by snouto on 15/05/15.
 */
public class PatientFileReader {


    private static final String[] REQUEST_FIELDS = {"PAT_NAME","RMC_NO","RMC_ORD",
    "T_PAT_NO","T_APPT_DATE","T_APPT_TYPE","T_UPD_USER","T_APPT_TIME","T_SCHEDULE_RULE_NO",
    "APPT_MADE_BY","APPT_DATE_H","USER_NAME","FILE_CURR_LOC","CF_APPT_TYPE"};

    private static  String TEMP_CLINIC_NAME = "";
    private static  String TEMP_CLINIC_DOC_NAME="";

    private  Request request = new Request();



    private static final int NumberOfFields = REQUEST_FIELDS.length;

    private static final String[] BATCH_REQUEST_FIELDS = {"CLINIC_NAME","DOC_NAME","CS_GROUP_COUNT"};


    private  String getBatchRequestField(String fieldName)
    {
        String foundField = null;

        for(String field : BATCH_REQUEST_FIELDS)
        {
            if(field.toLowerCase().equals(fieldName.toLowerCase()))
            {
                foundField = field;
                break;
            }
        }

        return foundField;
    }

    private  Map<String,String> getRequestMappedFields()
    {
        Map<String,String> mappedFields = new HashMap<String, String>();
        mappedFields.put("CLINIC_NAME","clinicName");
        mappedFields.put("DOC_NAME","requestingDocName");
        mappedFields.put("CS_GROUP_COUNT","csGroupCount");
        mappedFields.put("T_CLINIC_CODE","clinicCode");
        mappedFields.put("T_CLINIC_DOC_CODE","clinic_Doc_Code");

        mappedFields.put("RMC_ORD","rmc_ord");
        mappedFields.put("T_UPD_USER","t_upd_user");
        mappedFields.put("T_APPT_TIME","appointment_time");
        mappedFields.put("T_SCHEDULE_RULE_NO","t_schedule_ruleNo");
        mappedFields.put("PAT_NAME","patientName");
        mappedFields.put("RMC_NO","fileNumber");
        mappedFields.put("T_PAT_NO","patientNumber");
        mappedFields.put("T_APPT_DATE","appointment_Date");
        mappedFields.put("T_APPT_TYPE","appointment_Type");
        mappedFields.put("APPT_MADE_BY","appointment_made_by");
        mappedFields.put("APPT_DATE_H","appointment_date_h");
        mappedFields.put("USER_NAME","userName");
        mappedFields.put("FILE_CURR_LOC","fileCurrentLocation");
        mappedFields.put("CF_APPT_TYPE","cf_appointment_type");


        return mappedFields;
    }


    private   void autoRecursivelyBuildRequests(Node documentElement ,AtomicInteger controlNum,
                                                     List<Request> requests)
            throws NoSuchFieldException , IllegalAccessException , InvocationTargetException , NoSuchMethodException
    {
        if(controlNum.intValue() == NumberOfFields)
        {
            requests.add(request.clone());
            request = new Request();
            controlNum.set(0);
        }

        String elementName = documentElement.getNodeName();

        String foundAttr = containsField(elementName);

        if(foundAttr != null)
        {
            //get the attribute value
            String attrValue = documentElement.getTextContent();
            String mappedField = getRequestMappedFields().get(foundAttr);
            //set the value of the attribute to the attribute member in the request object
            boolean reflectingResult = setValueThroughReflection(request,mappedField,attrValue);

           if(reflectingResult) controlNum.incrementAndGet();
            else
           {
               System.out.println("Welcome mohamed");
           }

        }

        NodeList nodes = documentElement.getChildNodes();

        for(int i=0;i<nodes.getLength();i++)
        {
            Node currentNode = nodes.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE)
                autoRecursivelyBuildRequests(currentNode, controlNum, requests);

        }

    }

    private  String containsField(String fieldName)
    {
        String result = null;

        for(String field : REQUEST_FIELDS)
        {
            if(field.equalsIgnoreCase(fieldName.toLowerCase()))
            {
                result = field;
                break;
            }
        }

        return result;
    }


    public  List<String> readPatientFile(String patientFile) throws Exception
    {
        List<String> fields = new ArrayList<String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(patientFile));

        recursivelyReadDocument(doc.getDocumentElement(),fields);

        return fields;

    }

    private  Document buildDocument(String patientFile) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(patientFile));
        return doc;
    }

    public  List<Request> buildRequests(FileUploadWizardBean bean) throws  Exception
    {
        List<Request> requests = new ArrayList<Request>();

        AtomicInteger controlNum = new AtomicInteger(0);
        Document currentDoc = buildDocument(bean.getUploadedFile());
        String batchNumber = currentDoc.getDocumentElement().getNodeName();
        //recursivelyBuildRequests(currentDoc.getDocumentElement(),new Request(),bean,controlNum,requests);
       try
       {
           autoRecursivelyBuildRequests(currentDoc.getDocumentElement(),controlNum,requests);

       }catch (Exception s)
       {
           s.printStackTrace();
       }

        for(Request currentRequest : requests)
        {
            currentRequest.setBatchRequestNumber(batchNumber);
        }
        return requests;


    }




    private  void recursivelyBuildRequests(Node documentElement, Request request, FileUploadWizardBean bean,
                                                 AtomicInteger controlNum,List<Request> requests)
            throws NoSuchFieldException , IllegalAccessException , InvocationTargetException , NoSuchMethodException
    {

        if(controlNum.intValue() == NumberOfFields)
        {
            requests.add(request.clone());
            request = new Request();
            controlNum.set(0);
        }

        String elementName = documentElement.getNodeName();

        String foundAttr = bean.containsField(elementName);

        if(foundAttr != null)
        {
            //get the attribute value
            String attrValue = documentElement.getTextContent();
            //set the value of the attribute to the attribute member in the request object
            boolean reflectingResult = setValueThroughReflection(request,foundAttr,attrValue);

            if(reflectingResult) controlNum.incrementAndGet();
        }

        NodeList nodes = documentElement.getChildNodes();

        for(int i=0;i<nodes.getLength();i++)
        {
            Node currentNode = nodes.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE)
                recursivelyBuildRequests(currentNode,request,bean,controlNum,requests);
        }

    }

    private  boolean setValueThroughReflection(Request request, String foundAttr, String attrValue)
            throws NoSuchFieldException,IllegalAccessException,InvocationTargetException,NoSuchMethodException {


        Class<?> type = request.getClass();

        Method currentMethod = type.getDeclaredMethod("set" + capitalize(foundAttr), new Class[]{String.class});

        if(currentMethod != null)
            currentMethod.invoke(request,attrValue);

        return true;


    }

    private  String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private  void recursivelyReadDocument(Node documentElement, List<String> fields) {

        addField(documentElement.getNodeName(),fields);

        NodeList list = documentElement.getChildNodes();

        for(int i=0 ;i < list.getLength();i++)
        {
            Node currentNode = list.item(i);

            if(currentNode.getNodeType()==Node.ELEMENT_NODE)
                //read it recursively
            recursivelyReadDocument(currentNode,fields);
        }

    }

    private  void addField(String field,List<String> fields)
    {
        if(!fields.contains(field))
            fields.add(field);
    }


}

