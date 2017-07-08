package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;

public interface CompanyService {

    CompanyRegistration registerCompany(CompanyRegistration companyRegistration);

    Company findById(int id);
}
