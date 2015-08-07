package com.degla.utils;

import com.degla.db.models.Employee;
import com.degla.db.models.PatientFile;
import com.degla.db.models.Request;
import com.degla.db.models.RoleTypes;
import com.degla.system.SystemService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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

                if(assignedRequests == null || assignedRequests.size() <=0)
                {
                    wb.removeSheetAt(counter);
                    continue;
                }

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

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }
}
