package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TenderDAO extends DataTablesRepository<Tender, Integer> {

    @Query(value = "SELECT c.description, count(*) as count " +
            "FROM tender t inner join code_value c on t.status = c.code and c.type = 'tender_status' " +
            "where t.company_id = ?1 " +
            "group by c.description", nativeQuery = true)
    List<Object[]> getTenderStatusStatisticByCompany(int companyId);

    @Modifying
    @Query("update Tender t set t.status = 2, t.lastUpdatedBy = -1, t.lastUpdatedDate = CURRENT_TIMESTAMP where t.closedDate < CURRENT_TIMESTAMP and t.status = 1")
    void autoCloseTender();

    @Query(value = "select t from Tender t where t.closedDate < CURRENT_TIMESTAMP and t.status = 1")
    List<Tender> findClosingTender();

    @Modifying
    @Query("update Tender t set t.status = 2, t.lastUpdatedBy = -1, t.lastUpdatedDate = CURRENT_TIMESTAMP where t.id = ?1")
    void closeTender(int id);
}
