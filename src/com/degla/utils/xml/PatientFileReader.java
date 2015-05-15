package com.degla.utils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 15/05/15.
 */
public class PatientFileReader {


    public static List<String> readPatientFile(String patientFile) throws Exception
    {
        List<String> fields = new ArrayList<String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(patientFile));

        recursivelyReadDocument(doc.getDocumentElement(),fields);

        return fields;

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
