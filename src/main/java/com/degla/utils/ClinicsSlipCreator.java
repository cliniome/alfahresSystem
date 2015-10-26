package com.degla.utils;

import com.degla.db.models.Appointment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 09/10/15.
 */
public class ClinicsSlipCreator implements Serializable {


    private int fontSize = 100;
    private Font.FontFamily fontFamily = Font.FontFamily.HELVETICA;

    private List<String> writtenClinics = new ArrayList<String>();

    public Document getClinicsSlip(List<Appointment> appointments,OutputStream outputStream)
    {
        Document slip = new Document(new Rectangle(792,612));
        slip.setMargins(36, 72, 350, 180);
        slip.setPageSize(PageSize.A4);

        try
        {
            PdfWriter writer = PdfWriter.getInstance(slip,outputStream);

            if(appointments != null && appointments.size() > 0)
            {


                slip.open();

                //now create a page within each page only one paragraph that is composed of the clinic code
                for(Appointment app : appointments)
                {


                    String clinicCode = app.getClinicCode();

                    int clinicsNumber = getNumberOfAppointmentsForClinicCode(clinicCode,appointments);

                    if(clinicsNumber <= 0 ) continue;

                    if(writtenClinics.contains(clinicCode)) continue;
                    else
                        writtenClinics.add(clinicCode);

                    Paragraph paragraph = new Paragraph();
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    paragraph.setPaddingTop(50);
                    paragraph.setIndentationLeft(50);
                    paragraph.setIndentationRight(50);
                    paragraph.setSpacingBefore(50);
                    paragraph.setFont(new Font(getFontFamily(), getFontSize()));
                    paragraph.add(String.format("%s(%s)",clinicCode,String.valueOf(clinicsNumber)));

                    //add the paragraph to the current page and create a new page
                    slip.add(paragraph);
                    slip.newPage();
                }

            }else throw new Exception("Appointments can't be null in order to create clinics slips");

        }catch (Exception s)
        {
            s.printStackTrace();
        }
        finally {

            //close the document
            if(slip.isOpen())
                slip.close();

            return slip;
        }
    }


    private int getNumberOfAppointmentsForClinicCode(String clinicCode , List<Appointment> appointments)
    {

        List<String> previouslyRead = new ArrayList<String>();

        int number = 0;


        for(Appointment app : appointments)
        {
            if(previouslyRead.contains(app.getFileNumber())) continue;
            previouslyRead.add(app.getFileNumber());

            if(app.getClinicCode().toLowerCase().equals(clinicCode))
            {
                number += 1;
            }
        }

        return number;

    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Font.FontFamily getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(Font.FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }
}
