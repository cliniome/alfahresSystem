package com.degla.utils;

import com.degla.exceptions.BarcodeFormatException;

import java.io.Serializable;

/**
 * Created by snouto on 17/06/15.
 * This class is a barcode Helper class that will allow to do some transformations and validation
 * on barcode numbers that are passed to it
 */
public class BarcodeUtils implements Serializable {

    private String barcodeNumber;

    public static final String NEW_FILE_IDENTIFIER="01";
    public static final String NEW_SHELF_IDENTIFIER="00";
    public static final String NEW_TROLLEY_IDENTIFIER = "02";

    //The default constructor
    public BarcodeUtils(){}
    public BarcodeUtils(String barcodeNumber)
    {
        this.barcodeNumber = barcodeNumber;
    }

    public String getNewBarcodeStructure() throws BarcodeFormatException
    {
        if(barcodeNumber != null && barcodeNumber.length() > 0)
            return String.format("%s-%s",NEW_FILE_IDENTIFIER,barcodeNumber);
        else throw new BarcodeFormatException("Barcode Number shouldn't be null");
    }


    public boolean isNewFileStructure() throws BarcodeFormatException
    {
        if(barcodeNumber == null || barcodeNumber.length() <=0)
            throw new BarcodeFormatException("Your Barcode shouldn't be null or empty");

        //check to see if the current barcode number starts with the new file identifier
        return barcodeNumber.startsWith(NEW_FILE_IDENTIFIER);
    }

    public boolean isNewShelfStructure() throws  BarcodeFormatException
    {
        if(barcodeNumber == null || barcodeNumber.length() <=0)
            throw new BarcodeFormatException("Your Barcode shouldn't be null or empty");

        return barcodeNumber.startsWith(NEW_SHELF_IDENTIFIER);
    }

    public boolean isNewTrolleyStructure() throws BarcodeFormatException
    {
        if(barcodeNumber == null || barcodeNumber.length() <=0)
            throw new BarcodeFormatException("Your Barcode shouldn't be null or empty");

        return barcodeNumber.startsWith(NEW_TROLLEY_IDENTIFIER);
    }

}
