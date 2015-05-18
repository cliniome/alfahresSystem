package com.degla.utils.xml;

import com.degla.beans.files.FileUploadWizardBean;
import com.degla.db.models.Request;
import com.degla.exceptions.RequestException;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by snouto on 15/05/15.
 */
public class PatientFileReader {

    private static final int NumberOfFields = 7;


    public static List<String> readPatientFile(String patientFile) throws Exception
    {
        List<String> fields = new ArrayList<String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(patientFile));

        recursivelyReadDocument(doc.getDocumentElement(),fields);

        return fields;

    }

    private static Document buildDocument(String patientFile) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(patientFile));
        return doc;
    }

    public static List<Request> buildRequests(FileUploadWizardBean bean) throws  Exception
    {
        List<Request> requests = new ArrayList<Request>();

        AtomicInteger controlNum = new AtomicInteger(0);
        Document currentDoc = buildDocument(bean.getUploadedFile());
        recursivelyBuildRequests(currentDoc.getDocumentElement(),new Request(),bean,controlNum,requests);

        return requests;


    }

    private static void recursivelyBuildRequests(Node documentElement, Request request, FileUploadWizardBean bean,
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

    private static boolean setValueThroughReflection(Request request, String foundAttr, String attrValue)
            throws NoSuchFieldException,IllegalAccessException,InvocationTargetException,NoSuchMethodException {


        Class<?> type = request.getClass();

        Method currentMethod = type.getDeclaredMethod("set" + capitalize(foundAttr), new Class[]{String.class});

        if(currentMethod != null)
            currentMethod.invoke(request,attrValue);

        return true;

       /* Class<?> type = request.getClass();

        Field currentField = type.getDeclaredField(foundAttr);
        currentField.setAccessible(true);

        if(currentField != null)
        {
            currentField.set(request,attrValue);
            return true;

        }else return false;*/
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private static void recursivelyReadDocument(Node documentElement, List<String> fields) {

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

    private static void addField(String field,List<String> fields)
    {
        if(!fields.contains(field))
            fields.add(field);
    }


}

