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


    private int fontSize = 70;
    private Font.FontFamily fontFamily = Font.FontFamily.HELVETICA;

    private List<String> writtenClinics = new ArrayList<String>();


    public Document getClinicsSlipFromMetaData(List<Object[]> metadata,OutputStream outputStream)
    {
        Document slip = new Document(PageSize.A4_LANDSCAPE,0,0,0,0);
        slip.setMargins(36, 72, 120, 180);
       // slip.setPageSize(PageSize.A4_LANDSCAPE);

        try
        {
            PdfWriter writer = PdfWriter.getInstance(slip,outputStream);



            if(metadata != null && metadata.size() > 0)
            {
                slip.open();

                for(Object[] row : metadata)
                {
                    String clinicCode = String.valueOf(row[0]);
                    int clinicsNumber  = 0;
                    String clinics_Number_String = String.valueOf(row[1]);

                    String clinicName = row[2].toString();

                    if(clinics_Number_String != null && !clinics_Number_String.isEmpty())
                    {
                        clinicsNumber = Integer.parseInt(clinics_Number_String);
                    }

                    if(clinicsNumber <= 0 ) continue;

                    if(writtenClinics.contains(clinicCode)) continue;
                    else writtenClinics.add(clinicCode);

                    Paragraph paragraph = new Paragraph(50);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    paragraph.setPaddingTop(50);
                    paragraph.setIndentationLeft(50);
                    paragraph.setIndentationRight(50);
                    paragraph.setSpacingBefore(50);
                    paragraph.setFont(new Font(getFontFamily(), getFontSize()));
                    String text = String.format("(%s)",clinicCode);

                    paragraph.add(text);

                    slip.add(paragraph);

                    paragraph = new Paragraph(50);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    paragraph.setPaddingTop(50);
                    paragraph.setIndentationLeft(50);
                    paragraph.setIndentationRight(50);
                    paragraph.setSpacingBefore(50);
                    paragraph.setFont(new Font(getFontFamily(), 35));
                    text = String.format("%s",clinicName);
                    paragraph.add(text);

                    //add the paragraph to the current page and create a new page
                    slip.add(paragraph);



                    paragraph = new Paragraph(50);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    paragraph.setPaddingTop(50);
                    paragraph.setIndentationLeft(50);
                    paragraph.setIndentationRight(50);
                    paragraph.setSpacingBefore(50);
                    paragraph.setFont(new Font(getFontFamily(), 45));
                    text = String.format("Appt:[   ]/(%s)",clinicsNumber);

                    paragraph.add(text);


                    slip.add(paragraph);

                    slip.newPage();


                }

            }else throw new Exception("Clinic Slips Meta-Data can't be null");


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
