package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderDAO extends DataTablesRepository<Tender, Integer> {

    @Query(value = "SELECT c.description, count(*) as count " +
            "FROM tender t inner join code_value c on t.status = c.code and c.type = 'tender_status' " +
            "where t.company_id = ?1 " +
            "group by c.description", nativeQuery = true)
    List<Object[]> getTenderStatusStatisticByCompany(int companyId);
}
