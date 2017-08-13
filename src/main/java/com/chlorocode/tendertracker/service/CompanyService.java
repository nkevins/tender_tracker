package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.Company;

import java.util.List;

public interface CompanyService {

    Company registerCompany(Company companyRegistration);
    List<Company> findCompanyPendingApproval();
    CompanyRegistrationDetailsDTO findCompanyRegistrationById(int id);
    void approveCompanyRegistration(int id, int approvedBy);
    void rejectCompanyRegistration(int id, int rejectedBy);

    Company findById(int id);
    Company updateCompany(Company company);
}
