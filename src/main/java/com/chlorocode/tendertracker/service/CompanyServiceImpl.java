package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.CompanyRegistrationDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRegistrationDAO companyRegistrationDAO;
    private CompanyDAO companyDAO;

    public CompanyServiceImpl(CompanyRegistrationDAO companyRegistrationDAO, CompanyDAO companyDAO) {
        this.companyRegistrationDAO = companyRegistrationDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public CompanyRegistration registerCompany(CompanyRegistration companyRegistration) {
        companyRegistration.setCreatedDate(new Date());
        companyRegistration.setLastUpdatedDate(new Date());

        return companyRegistrationDAO.save(companyRegistration);
    }

    @Override
    public Company findById(int id) {
        return companyDAO.findOne(id);
    }
}
