package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyRegistrationDAO;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRegistrationDAO companyRegistrationDAO;

    public CompanyServiceImpl(CompanyRegistrationDAO companyRegistrationDAO) {
        this.companyRegistrationDAO = companyRegistrationDAO;
    }

    @Override
    public CompanyRegistration registerCompany(CompanyRegistration companyRegistration) {
        companyRegistration.setCreatedDate(new Date());
        companyRegistration.setLastUpdatedDate(new Date());

        return companyRegistrationDAO.save(companyRegistration);
    }
}
