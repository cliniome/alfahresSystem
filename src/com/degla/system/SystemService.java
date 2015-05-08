package com.degla.system;

import com.degla.api.Authenticator;
import com.degla.dao.ArchiveCabinetDAO;
import com.degla.dao.EmployeeDAO;
import com.degla.dao.FilesDAO;
import com.degla.dao.RoleDAO;
import com.degla.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 03/05/2015.
 */
@Component
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



















    public EmployeeDAO getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(EmployeeDAO employeeService) {
        this.employeeService = employeeService;
    }

    public FilesDAO getFilesService() {
        return filesService;
    }

    public void setFilesService(FilesDAO filesService) {
        this.filesService = filesService;
    }

    public Authenticator getAuthenticatorService() {
        return authenticatorService;
    }

    public void setAuthenticatorService(Authenticator authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    public ArchiveCabinetDAO getCabinetsService() {
        return cabinetsService;
    }

    public void setCabinetsService(ArchiveCabinetDAO cabinetsService) {
        this.cabinetsService = cabinetsService;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public RoleDAO getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleDAO roleService) {
        this.roleService = roleService;
    }
}
