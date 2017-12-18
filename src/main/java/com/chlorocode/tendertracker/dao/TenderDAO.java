package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
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

    @Query(value = "select t.ref_no, t.title, c1.description as tender_type, tc.name, open_date, closed_date, c2.description as status " +
            "from tender t, tender_category tc, code_value c1, code_value c2 " +
            "where tc.id = t.tender_category_id and t.tender_type = c1.code and c1.type = 'tender_type' " +
            "and t.status = c2.code and c2.type = 'tender_status' and t.open_date >= ?1 and t.open_date <= ?2",
            nativeQuery = true)
    List<Object[]> findAllByDateRange(Date startDate, Date endDate);
}
