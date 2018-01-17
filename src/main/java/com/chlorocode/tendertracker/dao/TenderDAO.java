package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select t.ref_no, t.title, c1.description as tender_type, tc.name, open_date, closed_date, c2.description as status " +
            "from tender t, tender_category tc, code_value c1, code_value c2 " +
            "where tc.id = t.tender_category_id and t.tender_type = c1.code and c1.type = 'tender_type' " +
            "and t.status = c2.code and c2.type = 'tender_status' and t.open_date >= ?1 and t.open_date <= ?2 " +
            "and t.closed_date >= ?3 and t.closed_date <= ?4 " +
            "and t.tender_category_id = ?5 and t.status = ?6 ",
            nativeQuery = true)
    List<Object[]> findAllByDateRange(Date openDateFrom, Date openDateTo,
                                      Date closeDateFrom, Date closeDateTo,
                                      String category, String status);

    @Modifying
    @Query("update Tender t set t.status = 2, t.lastUpdatedBy = -1, t.lastUpdatedDate = CURRENT_TIMESTAMP where t.closedDate < CURRENT_TIMESTAMP and t.status = 1")
    void autoCloseTender();

    @Query(value = "select t from Tender t where t.closedDate < CURRENT_TIMESTAMP and t.status = 1")
    List<Tender> findClosingTender();

    @Modifying
    @Query("update Tender t set t.status = 2, t.lastUpdatedBy = -1, t.lastUpdatedDate = CURRENT_TIMESTAMP where t.id = ?1")
    void closeTender(int id);

    @Query("select count(v) from Tender t join t.tenderVisits v where t.id = ?1")
    Integer getNumberOfVisit(int tenderId);

    @Query("select count(distinct v.ipAddress) from Tender t join t.tenderVisits v where t.id = ?1")
    Integer getNumberOfUniqueVisit(int tenderId);

    @Query("select v.country, count(v) from Tender t join t.tenderVisits v where t.id = ?1 group by v.country order by count(v) desc")
    List<Object[]> getTopCountryVisitor(int tenderId, Pageable pageable);

    @Query(value = "select case when status=1 then 'Open' when status=2 then 'Closed' else 'Others' end as Status, " +
            "count(*) as Count from tender where open_date >= ?1 and open_date <= ?2 group by status",
            nativeQuery = true)
    List<Object[]> getTenderSummary(Date startDate, Date endDate);

    @Query(value = "select distinct t from Tender t join t.invitedCompanies c where c.id = ?1")
    List<Tender> findTenderByInvitedCompany(int companyId);

}
