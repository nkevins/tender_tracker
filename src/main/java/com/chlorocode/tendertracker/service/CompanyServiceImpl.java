package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.UenEntity;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Implementation of CompanyService.
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyDAO companyDAO;

    private CodeValueService codeValueService;
    private UserService userService;
    private UserRoleService userRoleService;
    private NotificationService notificationService;
    private UenEntityService uenEntService;

    /**
     * Constructor.
     *
     * @param companyDAO CompanyDAO
     * @param codeValueService CodeValueService
     * @param userService UserService
     * @param userRoleService UserRoleService
     * @param notificationService NotificationService
     * @param uenEntService UenEntityService
     */
    @Autowired
    public CompanyServiceImpl(CompanyDAO companyDAO, CodeValueService codeValueService, UserService userService
                                , UserRoleService userRoleService, NotificationService notificationService
                                , UenEntityService uenEntService) {
        this.companyDAO = companyDAO;
        this.codeValueService = codeValueService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.notificationService = notificationService;
        this.uenEntService = uenEntService;
    }

    @Override
    public String registerCompany(Company companyRegistration) throws ApplicationException {
        List<Company> compList = findCompanyByUen(companyRegistration.getUen());

        if(compList != null && compList.size() > 0){
            return "This company UEN already exist";
        }

        UenEntity uenEnt = uenEntService.findByUen(companyRegistration.getUen());
        if (uenEnt == null) {
            return "Invalid UEN";
        }
        if (!companyRegistration.isPostalCodeValid()) {
            throw new ApplicationException("Invalid postal code");
        }

        companyRegistration.setCreatedDate(new Date());
        companyRegistration.setLastUpdatedDate(new Date());

        Company company = companyDAO.save(companyRegistration);

        Map<String, Object> params = new HashMap<>();
        params.put(TTConstants.PARAM_COMPANY, company);
        User user = userService.findById(company.getCreatedBy());
        params.put(TTConstants.PARAM_EMAIL, user.getEmail());
        notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.company_reg_noti, params);

        return null;
    }

    @Override
    public List<Company> findCompanyPendingApproval() {
        return companyDAO.findCompanyPendingApproval();
    }

    @Override
    public CompanyRegistrationDetailsDTO findCompanyRegistrationById(int id) {
        Company company = companyDAO.findOne(id);

        if (company == null) {
            return null;
        }

        CompanyRegistrationDetailsDTO reg = new CompanyRegistrationDetailsDTO();
        reg.setId(company.getId());
        reg.setName(company.getName());
        reg.setGstRegNo(company.getGstRegNo());
        reg.setUen(company.getUen());
        reg.setType(codeValueService.getDescription("company_type", company.getType()));
        reg.setAreaOfBusiness(codeValueService.getDescription("area_of_business", company.getAreaOfBusiness()));
        reg.setAddress1(company.getAddress1());
        reg.setAddress2(company.getAddress2());
        reg.setPostalCode(company.getPostalCode());
        reg.setCity(company.getCity());
        reg.setState(company.getState());
        reg.setProvince(company.getProvince());
        reg.setCountry(company.getCountry().getName());
        reg.setApplicant(userService.findById(company.getCreatedBy()));
        reg.setApplicationDate(company.getCreatedDate());
        reg.setPrincipleActivity(company.getPrincpleBusinessActivity());
        reg.setActive(company.isActive());

        if(company.getStatus() == 1){
            reg.setStatus("Approved");
        }else if(company.getStatus() == 2){
            reg.setStatus("Rejected");
        }

        if(company.isActive()){
            reg.setCompanyStatus("Active");
        }else{
            reg.setCompanyStatus("Blacklisted");
        }
        return reg;
    }

    @Override
    @Transactional
    public void approveCompanyRegistration(int id, int approvedBy) {
        Company company = companyDAO.findOne(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        } else {
            company.setStatus(1);
            company.setLastUpdatedBy(approvedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);

            // Assign creator as Admin
            User user = userService.findById(company.getCreatedBy());
            List<Role> roles = new LinkedList<>();
            roles.add(userRoleService.findRoleById(2));
            userRoleService.addUserRole(user, roles, company, approvedBy);

            Map<String, Object> params = new HashMap<>();
            params.put(TTConstants.PARAM_APPROVAL_ACTION, TTConstants.APPROVED);
            params.put(TTConstants.PARAM_COMPANY_ID, company.getId());
            params.put(TTConstants.PARAM_COMPANY_NAME, company.getName());
            params.put(TTConstants.PARAM_EMAIL, user.getEmail());
            notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.company_reviewed_noti, params);
        }
    }

    @Override
    public void rejectCompanyRegistration(int id, int rejectedBy) {
        Company company = companyDAO.findOne(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        } else {
            company.setStatus(2);
            company.setLastUpdatedBy(rejectedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);

            // Assign creator as Admin
            User user = userService.findById(company.getCreatedBy());
            if (user != null) {
                Map<String, Object> params = new HashMap<>();
                params.put(TTConstants.PARAM_APPROVAL_ACTION, TTConstants.REJECTED);
                params.put(TTConstants.PARAM_COMPANY_ID, company.getId());
                params.put(TTConstants.PARAM_COMPANY_NAME, company.getName());
                params.put(TTConstants.PARAM_EMAIL, user.getEmail());
                notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.company_reviewed_noti, params);
            }
        }
    }

    @Override
    public boolean blacklistCompany(int id, int rejectedBy) {
        Company company = companyDAO.findOne(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        }else{
            company.setActive(false);
            company.setLastUpdatedBy(rejectedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);

            // Send email notification to blacklisted company
            Set<String> adminEmails = userRoleService.findCompanyAdminEmails(company.getId());
            if (adminEmails != null && adminEmails.size()> 0) {
                Map<String, Object> params = new HashMap<>();
                params.put(TTConstants.PARAM_COMPANY, company);
                params.put(TTConstants.PARAM_EMAILS, adminEmails.toArray(new String[adminEmails.size()]));
                notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.company_blacklisted_noti, params);
            }

            return true;
        }
    }

    @Override
    public boolean unblacklistCompany(int id, int rejectedBy) {
        Company company = companyDAO.findOne(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        }else{
            company.setActive(true);
            company.setLastUpdatedBy(rejectedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);
            return true;
        }
    }

    @Override
    public Company findById(int id) {
        return companyDAO.findOne(id);
    }

    @Override
    public Company updateCompany(Company company) throws ApplicationException {
        if (!company.isPostalCodeValid()) {
            throw new ApplicationException("Invalid postal code");
        }

        return companyDAO.save(company);
    }

    @Override
    public List<Company> findCompanyByUen(String uen) {
        return companyDAO.findCompanyByUen(uen);
    }

    @Override
    public List<Company> findCompanyByCreatedBy(int userId) {
        return companyDAO.findCompanyByCreatedBy(userId);
    }
}
