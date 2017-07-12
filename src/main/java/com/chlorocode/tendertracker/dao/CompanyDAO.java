package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDAO extends DataTablesRepository<Company, Integer> {

    @Query("select c from Company c where c.status = 0 and c.id = ?1")
    Company findCompanyRegistrationById(int id);

}
