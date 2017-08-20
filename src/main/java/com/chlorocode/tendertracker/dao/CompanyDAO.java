package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyDAO extends DataTablesRepository<Company, Integer> {

    @Query("select c from Company c where c.status = 0")
    List<Company> findCompanyPendingApproval();

    @Query("select c from Company c where c.uen = ?1")
    List<Company> findCompanyByUen(String uen);
}
