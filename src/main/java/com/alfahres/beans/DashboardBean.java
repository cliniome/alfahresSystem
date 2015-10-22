package com.alfahres.beans;

import com.degla.db.models.*;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 03/05/2015.
 */
public class DashboardBean {


    private
    SystemService systemService;

    private ActorEO account;

    private int currentPageNumber;


    @PostConstruct
    public void init()
    {
        try {
            systemService = SpringSystemBridge.services();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getTotalNewFiles()
    {
        try
        {

            return systemService.getAppointmentManager().getTotalNewAppointments();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long getTotalWatchList()
    {
        try
        {

            return systemService.getAppointmentManager().getTotalWatchList();

        }catch (Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long getTotalReceivedByCoordinator()
    {
        try
        {

            Long inSortingfiles = systemService.getFilesService().getFilesCountForState(FileStates.COORDINATOR_IN);

            if(inSortingfiles == null) return 0L;
            else
                return inSortingfiles.longValue();

        }catch (Exception s)
        {
            return 0L;
        }
    }


    public long getTotalFilesUnderSorting()
    {
        try
        {

            Long inSortingfiles = systemService.getFilesService().getFilesCountForState(FileStates.OUT_OF_CABIN);

            if(inSortingfiles == null) return 0L;
            else
                return inSortingfiles.longValue();

        }catch (Exception s)
        {

            return 0L;
        }
    }


    public long getTotalReceivedByReceptionist()
    {
        try
        {
            long receivedFilesCount = systemService.getFilesService().getFilesCountForState(FileStates.RECEPTIONIST_IN);

            return receivedFilesCount;

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }


    public boolean is_InpatientCoordinator(){

        RoleTypes role_Type = RoleTypes.valueOf(getAccount().getRole().getName());

        return role_Type.isInPatientActor();
    }


    public List<PatientFile> getMissingFiles()
    {
        try
        {
            RoleTypes currentType = RoleTypes.valueOf(getAccount().getRole().getName());

            List<PatientFile> missingFiles = systemService.getFilesService().getMissingFiles(currentType.isInPatientActor());

            if(missingFiles != null && missingFiles.size() > 0) return missingFiles;
            else return new ArrayList<PatientFile>();

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<PatientFile>();
        }
    }


    public long getTotalArchivedFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalCheckedInFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long getTotalTransferredFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalTransferredFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long getTotalDistributedFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalDistributedFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }

    public long selectTotalCheckedOutFiles(boolean inpatient)
    {
        try
        {

            return systemService.getFilesService().getTotalCheckedOutByType(inpatient);

        }catch (Exception s)
        {
            s.printStackTrace();
            return -1L;
        }
    }

    public long getTotalCheckedOutFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalCheckedOutFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }


    public long getFilesForState(String state){

        if(state == null || state.isEmpty())
            return 0L;
        else{


            FileStates currentState = FileStates.valueOf(state);
            return systemService.getFilesService().getFilesCountForState(currentState);


        }

    }


    public long getTotalMissingFiles()
    {
        try
        {
            return systemService.getFilesService().getTotalMissingFiles();

        }catch(Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }


    public long getTotalTemporaryStored(){

        try
        {

            return systemService.getFilesService().getFilesCountForState(FileStates.TEMPORARY_STORED);

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }


    public long getTotalProcessingCoordinator(){

        try
        {
            return systemService.getFilesService().getFilesCountForState(FileStates.PROCESSING_COORDINATOR);

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }

    public long getTotalCodingCoordinator(){

        try
        {
            return systemService.getFilesService().getFilesCountForState(FileStates.CODING_COORDINATOR);

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }

    public long getTotalAnalysisCoordinator(){

        try
        {

            return systemService.getFilesService().getFilesCountForState(FileStates.ANALYSIS_COORDINATOR);

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }

    public long getTotalInCompleteCoordinator(){

        try
        {
            return systemService.getFilesService().getFilesCountForState(FileStates.INCOMPLETE_COORDINATOR);

        }catch (Exception s)
        {
            s.printStackTrace();
            return 0L;
        }
    }

    public void onUpdateAccount(ActionEvent event)
    {
        try
        {
            //updating the current employee
            Employee emp = (Employee)this.getAccount();
            boolean result = systemService.getEmployeeService().updateEntity(emp);

            if(result)
            {
                WebUtils.addMessage("Profile has been updated Successfully");
            }
            else
            {
                WebUtils.addMessage("There is a problem during updating Your Account,Contact System Support");
            }

        }catch(Exception s)
        {
            s.printStackTrace();
        }
    }


    public long getTotalEmployees()
    {
        try
        {
            return systemService.getEmployeeService().getMaxResults();

        }catch (Exception s)
        {
            s.printStackTrace();
            return -1;
        }
    }


    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public ActorEO getAccount() {
        return account;
    }

    public void setAccount(ActorEO account) {
        this.account = account;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }
}
