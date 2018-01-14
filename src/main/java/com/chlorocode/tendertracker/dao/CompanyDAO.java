package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CompanyDAO extends DataTablesRepository<Company, Integer> {

    @Query("select c from Company c where c.status = 0")
    List<Company> findCompanyPendingApproval();

    @Query("select c from Company c where c.uen = ?1")
    List<Company> findCompanyByUen(String uen);

    @Query("select c from Company c where c.createdBy = ?1")
    List<Company> findCompanyByCreatedBy(int userId);

    @Query("select c from Company c where c.name LIKE CONCAT('%',?1,'%') and status = 1")
    List<Company> findCompanyByName(String name);

    @Query(value = "select case when status=0 then 'Pending Approval' when status=1 then 'Approved' when status=2 " +
            "then 'Rejected' end as Status, count(*) as Count from company where  created_date >= ?1 " +
            "and created_date <= ?2 group by status",
            nativeQuery = true)
    List<Object[]> getCompanySummary(Date startDate, Date endDate);
}
