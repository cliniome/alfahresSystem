package com.degla.utils;

import com.degla.db.models.Employee;
import com.degla.db.models.PatientFile;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SystemService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import sun.java2d.pipe.SpanShapeRenderer;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 13/07/15.
 */
public class ExcelFileBuilder {


    private SystemService systemService;

    public ExcelFileBuilder(SystemService service)
    {
        this.systemService = service;
    }


    private List<Employee> getKeepers()
    {
        try
        {
           return systemService.getEmployeeService().getEmployeesByRole(RoleTypes.KEEPER.toString(),true);

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public Workbook extractOnly(Date appointmentDate , List<PatientFile> availableFiles)
    {
        try
        {
            if(availableFiles == null || availableFiles.size() <= 0 )
                return null;

            Workbook wb = new HSSFWorkbook();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String appointmentDateString = format.format(appointmentDate);

            Sheet currentSheet = wb.createSheet(appointmentDateString);

            this.addFileHeaders(currentSheet);

            for(int i = 0 ; i < availableFiles.size() ;i++)
            {
                PatientFile patientFile = availableFiles.get(i);
                Row currentRow = currentSheet.createRow(i+1);

                //File Number
                Cell fileNumberCell = currentRow.createCell(0);
                fileNumberCell.setCellValue(patientFile.getFileID());
                //Patient Name
                Cell patientNameCell = currentRow.createCell(1);
                patientNameCell.setCellValue(patientFile.getPatientName());
                //Appointment Date
                Cell appointmentDateCell = currentRow.createCell(2);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                appointmentDateCell.setCellValue(formatter.format(patientFile.getCurrentStatus().getAppointment_Date_G()));

                //State
                Cell stateCell = currentRow.createCell(3);
                stateCell.setCellValue(patientFile.getCurrentStatus().getState().toString());

                //Clinic Code
                Cell clinicCodeCell = currentRow.createCell(4);
                clinicCodeCell.setCellValue(patientFile.getCurrentStatus().getClinicCode());
            }


            return wb;



        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public Workbook buildExcelFile()
    {
        int counter = 0;

        try
        {
            Workbook wb = new HSSFWorkbook();

            List<Employee> keepers = this.getKeepers();

            if(keepers == null||keepers.size() <=0) return null;

            for(Employee keeper : keepers)
            {
                Sheet workSheet = wb.createSheet(keeper.getfullName());

                this.addHeaders(workSheet);

                List<Request> assignedRequests = systemService.getRequestsManager()
                        .getNewRequestsFor(keeper.getUsername());


                Collections.sort(assignedRequests, new Comparator<Request>() {
                    @Override
                    public int compare(Request first, Request second) {

                        BarcodeUtils firstUtils = new BarcodeUtils(first.getFileNumber());
                        BarcodeUtils secondUtils = new BarcodeUtils(second.getFileNumber());
                        int firstCabinId = Integer.parseInt(firstUtils.getCabinID()+firstUtils.getColumnNo());
                        int secondCabinId = Integer.parseInt(secondUtils.getCabinID()+secondUtils.getColumnNo());

                        if(firstCabinId > secondCabinId)
                            return 1;
                        else if (firstCabinId == secondCabinId) return 0;
                        else return -1;

                    }
                });

                if(assignedRequests == null || assignedRequests.size() <=0)
                {
                    wb.removeSheetAt(counter);
                    continue;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");

                for(int i =0 ; i < assignedRequests.size();i++)
                {
                    Request req = assignedRequests.get(i);

                    //Check to see if that request exists in the patient Files , if yes , that means it was built before
                    PatientFile file = systemService.getFilesService().getFileWithNumber(req.getFileNumber());

                    if(file != null)
                        continue;

                    Row currentRow = workSheet.createRow(i+1);

                    //File Number Cell
                    Cell fileNumberCell = currentRow.createCell(0);
                    fileNumberCell.setCellValue(assignedRequests.get(i).getFileNumber());
                    //Patient Number Cell
                    Cell patientNumberCell = currentRow.createCell(1);
                    patientNumberCell.setCellValue(assignedRequests.get(i).getPatientNumber());
                    //Appointment Date
                    Cell dateCell = currentRow.createCell(2);
                    dateCell.setCellValue(formatter.format(assignedRequests.get(i).getAppointment_Date()));

                }



                ++counter;
            }

            return wb;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    private void addHeaders(Sheet currentSheet)
    {
        try
        {
            Row headerRow = currentSheet.createRow(0);
            Cell fileNumber  =headerRow.createCell(0);
            fileNumber.setCellValue("File Number");
            Cell patientNumber = headerRow.createCell(1);
            patientNumber.setCellValue("Patient Number");

            //Date Object
            Cell date = headerRow.createCell(2);
            date.setCellValue("Appointment_Date");

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    private void addFileHeaders(Sheet currentSheet)
    {
        try
        {
            Row headerRow = currentSheet.createRow(0);
            Cell fileNumber  =headerRow.createCell(0);
            fileNumber.setCellValue("File Number");
            Cell patientNumber = headerRow.createCell(1);
            patientNumber.setCellValue("Patient Name");
            //Date Object
            Cell date = headerRow.createCell(2);
            date.setCellValue("Appointment_Date");
            //State
            Cell stateCell = headerRow.createCell(3);
            stateCell.setCellValue("State");
            //Clinic Code
            Cell clinicCell = headerRow.createCell(4);
            clinicCell.setCellValue("Clinic Code");

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }
}
