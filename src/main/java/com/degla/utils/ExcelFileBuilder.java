package com.degla.utils;

import com.degla.db.models.*;
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

    private boolean watchList;

    public ExcelFileBuilder(SystemService service)
    {
        this.systemService = service;
    }

    public ExcelFileBuilder(SystemService service,boolean watchList)
    {
        this.systemService = service;
        this.watchList = watchList;
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

    public Workbook extractOnly(String status , List<PatientFile> availableFiles)
    {
        try
        {
            if(availableFiles == null || availableFiles.size() <= 0 )
                return null;

            Workbook wb = new HSSFWorkbook();



            Sheet currentSheet = wb.createSheet(status);

            this.addFileHeaders(currentSheet);

            for(int i = 0 ; i < availableFiles.size() ;i++)
            {
                PatientFile patientFile = availableFiles.get(i);
                Row currentRow = currentSheet.createRow(i + 1);

                //File Number
                Cell fileNumberCell = currentRow.createCell(0);
                fileNumberCell.setCellValue(patientFile.getFileID());
                //Patient Name
                Cell patientNameCell = currentRow.createCell(1);
                patientNameCell.setCellValue(patientFile.getPatientName());
                //Appointment Date
                Cell appointmentDateCell = currentRow.createCell(2);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                appointmentDateCell.setCellValue(formatter.format(patientFile.getCurrentStatus().getAppointment().getAppointment_Date()));

                //State
                Cell stateCell = currentRow.createCell(3);
                stateCell.setCellValue(patientFile.getCurrentStatus().getState().toString());

                //Clinic Code
                Cell clinicCodeCell = currentRow.createCell(4);
                clinicCodeCell.setCellValue(patientFile.getCurrentStatus().getAppointment().getClinicCode());


                //Cabin number

                Cell cabinCell = currentRow.createCell(5);
                BarcodeUtils utils = new BarcodeUtils(patientFile.getFileID());
                cabinCell.setCellValue(utils.getCabinID());
            }


            return wb;



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
                Row currentRow = currentSheet.createRow(i + 1);

                //File Number
                Cell fileNumberCell = currentRow.createCell(0);
                fileNumberCell.setCellValue(patientFile.getFileID());
                //Patient Name
                Cell patientNameCell = currentRow.createCell(1);
                patientNameCell.setCellValue(patientFile.getPatientName());
                //Appointment Date
                Cell appointmentDateCell = currentRow.createCell(2);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                appointmentDateCell.setCellValue(formatter.format(patientFile.getCurrentStatus().getAppointment().getAppointment_Date()));

                //State
                Cell stateCell = currentRow.createCell(3);
                stateCell.setCellValue(patientFile.getCurrentStatus().getState().toString());

                //Clinic Code
                Cell clinicCodeCell = currentRow.createCell(4);
                clinicCodeCell.setCellValue(patientFile.getCurrentStatus().getAppointment().getClinicCode());


                //Cabin number

                Cell cabinCell = currentRow.createCell(5);
                BarcodeUtils utils = new BarcodeUtils(patientFile.getFileID());
                cabinCell.setCellValue(utils.getCabinID());
            }


            return wb;



        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }


    public Workbook buildExcelNow()
    {
        if(watchList)
        {
            //download the watch list requests only

            return buildWatchList();
        }else
        {
            //build the new available Requests Only
            return buildExcelFile();
        }
    }

    private Workbook buildWatchList() {

        try
        {
            Workbook wb = new HSSFWorkbook();

            Sheet workSheet = wb.createSheet("WatchList");
            this.addHeaders(workSheet);

            List<Appointment> watchListRequests = systemService.getAppointmentManager().getAllWatchListRequests();

            Collections.sort(watchListRequests, new Comparator<Appointment>() {
                @Override
                public int compare(Appointment first, Appointment second) {

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

            if(watchListRequests == null || watchListRequests.size() <=0)
            {
                wb.removeSheetAt(0);
                return wb;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");

            for(int i = 0 ; i < watchListRequests.size();i++)
            {
                Appointment watchRequest = watchListRequests.get(i);

                Row currentRow = workSheet.createRow(i+1);

                //File Number Cell
                Cell fileNumberCell = currentRow.createCell(0);
                fileNumberCell.setCellValue(watchRequest.getFileNumber());
                //Patient Number Cell
                Cell patientNumberCell = currentRow.createCell(1);
                patientNumberCell.setCellValue(watchRequest.getPatientNumber());
                //Appointment Date
                Cell dateCell = currentRow.createCell(2);
                dateCell.setCellValue(formatter.format(watchRequest.getAppointment_Date()));


                //Cabin Cell
                BarcodeUtils barcodeUtils = new BarcodeUtils(watchRequest.getFileNumber());

                Cell cabinCell = currentRow.createCell(3);
                cabinCell.setCellValue(barcodeUtils.getCabinID());



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

                List<Appointment> assignedRequests = systemService.getAppointmentManager()
                        .getNewRequestsFor(keeper.getUsername());


                Collections.sort(assignedRequests, new Comparator<Appointment>() {
                    @Override
                    public int compare(Appointment first, Appointment second) {

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
                    Appointment req = assignedRequests.get(i);

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


                    //Cabin Cell
                    BarcodeUtils barcodeUtils = new BarcodeUtils(req.getFileNumber());

                    Cell cabinCell = currentRow.createCell(3);
                    cabinCell.setCellValue(barcodeUtils.getCabinID());

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

            Cell cabin = headerRow.createCell(3);
            cabin.setCellValue("Cabin");

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

            Cell cabinCell = headerRow.createCell(5);
            cabinCell.setCellValue("Cabin Number");

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    public boolean isWatchList() {
        return watchList;
    }

    public void setWatchList(boolean watchList) {
        this.watchList = watchList;
    }
}
