package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.CompanyRegistrationDAO;
import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRegistrationDAO companyRegistrationDAO;
    private CompanyDAO companyDAO;

    private CodeValueService codeValueService;
    private UserService userService;

    public CompanyServiceImpl(CompanyRegistrationDAO companyRegistrationDAO, CompanyDAO companyDAO,
                              CodeValueService codeValueService, UserService userService) {
        this.companyRegistrationDAO = companyRegistrationDAO;
        this.companyDAO = companyDAO;
        this.codeValueService = codeValueService;
        this.userService = userService;
    }

    @Override
    public Company registerCompany(Company companyRegistration) {
        companyRegistration.setCreatedDate(new Date());
        companyRegistration.setLastUpdatedDate(new Date());

        return companyDAO.save(companyRegistration);
    }

    @Override
    public CompanyRegistrationDetailsDTO findCompanyRegistrationById(int id) {
        Company company = companyDAO.findCompanyRegistrationById(id);

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
        reg.setCountry(company.getCountry());
        reg.setApplicant(userService.findById(company.getCreatedBy()));
        reg.setApplicationDate(company.getCreatedDate());

        return reg;
    }

    @Override
    public void approveCompanyRegistration(int id, int approvedBy) {
        Company company = companyDAO.findCompanyRegistrationById(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        } else {
            company.setStatus(1);
            company.setLastUpdatedBy(approvedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);
        }
    }

    @Override
    public void rejectCompanyRegistration(int id, int rejectedBy) {
        Company company = companyDAO.findCompanyRegistrationById(id);

        if (company == null) {
            throw new ApplicationException("Company Registration not found");
        } else {
            company.setStatus(2);
            company.setLastUpdatedBy(rejectedBy);
            company.setLastUpdatedDate(new Date());

            companyDAO.save(company);
        }
    }

    @Override
    public Company findById(int id) {
        return companyDAO.findOne(id);
    }
}
