package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.Company;

import java.util.List;

/**
 * Service interface for company.
 */
public interface CompanyService {

    /**
     * This method is used to save company registration record.
     *
     * @param companyRegistration company object to be saved
     * @return String
     */
    String registerCompany(Company companyRegistration);

    /**
     * This method is used to find company with pending approval status.
     *
     * @return list of Company
     */
    List<Company> findCompanyPendingApproval();

    /**
     * This method is used to find company registration record by company id.
     *
     * @param id unique identifier of the company
     * @return CompanyRegistrationDetailsDTO
     * @see com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDTO
     */
    CompanyRegistrationDetailsDTO findCompanyRegistrationById(int id);

    /**
     * This method is used to approve company registration.
     *
     * @param id unique identifier of the company
     * @param approvedBy user id who approve the company
     */
    void approveCompanyRegistration(int id, int approvedBy);

    /**
     * This method is used to reject company registration.
     *
     * @param id unique identifier of the company
     * @param rejectedBy user id who reject the company
     */
    void rejectCompanyRegistration(int id, int rejectedBy);

    /**
     * This method is used to blacklist company.
     *
     * @param id unique identifier of the company
     * @param blacklistedBy user id who blacklist the company
     * @return boolean
     */
    boolean blacklistCompany(int id, int blacklistedBy);

    /**
     * This method is used to remove company blacklist.
     *
     * @param id unique identifier of the company
     * @param unblacklistedBy user id who remove the company blacklist
     * @return boolean
     */
    boolean unblacklistCompany(int id, int unblacklistedBy);

    /**
     * This method is used to get a company by company id.
     *
     * @param id unique identifier of the company
     * @return Company
     */
    Company findById(int id);

    /**
     * This method is used to update company.
     *
     * @param company company object to be updated
     * @return Company
     */
    Company updateCompany(Company company);

    /**
     * This method is used to find company by UEN.
     *
     * @param uen uen no
     * @return list of Company
     */
    List<Company> findCompanyByUen(String uen);

    /**
     * This method is used to find company by creator.
     *
     * @param userId unique identifier of the user
     * @return list of Company
     */
    List<Company> findCompanyByCreatedBy(int userId);
}
