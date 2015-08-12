package com.degla.system;

import com.degla.api.Authenticator;
import com.degla.dao.*;
import com.degla.security.LoginService;
import com.degla.utils.DatePatternsBean;
import com.degla.utils.FileRouter;
import com.degla.utils.SystemSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
@Scope("prototype")
public class SystemService {

    @Autowired
    private
    EmployeeDAO employeeService;
    @Autowired
    private
    FilesDAO filesService;
    @Autowired
    private
    Authenticator authenticatorService;
    @Autowired
    private ArchiveCabinetDAO cabinetsService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RoleDAO roleService;
    @Autowired
    private SystemSettings systemSettings;
    @Autowired
    private RequestsDAO requestsManager;
    @Autowired
    private FileRouter fileRouter;
    @Autowired
    private ClinicDAO clinicManager;
    @Autowired
    private FileHistoryDAO fileHistoryDAO;
    @Autowired
    private TransferDAO transferManager;

    @Autowired
    private DatePatternsBean datePatternsBean;



    public synchronized EmployeeDAO getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeDAO employeeService) {
        this.employeeService = employeeService;
    }

    public synchronized FilesDAO getFilesService() {
        return filesService;
    }

    public void setFilesService(FilesDAO filesService) {
        this.filesService = filesService;
    }

    public synchronized Authenticator getAuthenticatorService() {
        return authenticatorService;
    }

    public void setAuthenticatorService(Authenticator authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    public synchronized ArchiveCabinetDAO getCabinetsService() {
        return cabinetsService;
    }

    public void setCabinetsService(ArchiveCabinetDAO cabinetsService) {
        this.cabinetsService = cabinetsService;
    }

    public synchronized LoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public synchronized RoleDAO getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleDAO roleService) {
        this.roleService = roleService;
    }

    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public void setSystemSettings(SystemSettings systemSettings) {
        this.systemSettings = systemSettings;
    }

    public synchronized RequestsDAO getRequestsManager() {
        return requestsManager;
    }

    public void setRequestsManager(RequestsDAO requestsManager) {
        this.requestsManager = requestsManager;
    }

    public synchronized FileRouter getFileRouter() {
        return fileRouter;
    }

    public void setFileRouter(FileRouter fileRouter) {
        this.fileRouter = fileRouter;
    }


    public synchronized ClinicDAO getClinicManager() {
        return clinicManager;
    }

    public void setClinicManager(ClinicDAO clinicManager) {
        this.clinicManager = clinicManager;
    }

    public synchronized FileHistoryDAO getFileHistoryDAO() {
        return fileHistoryDAO;
    }

    public void setFileHistoryDAO(FileHistoryDAO fileHistoryDAO) {
        this.fileHistoryDAO = fileHistoryDAO;
    }

    public synchronized TransferDAO getTransferManager() {
        return transferManager;
    }

    public void setTransferManager(TransferDAO transferManager) {
        this.transferManager = transferManager;
    }

    public DatePatternsBean getDatePatternsBean() {
        return datePatternsBean;
    }

    public void setDatePatternsBean(DatePatternsBean datePatternsBean) {
        this.datePatternsBean = datePatternsBean;
    }
}
