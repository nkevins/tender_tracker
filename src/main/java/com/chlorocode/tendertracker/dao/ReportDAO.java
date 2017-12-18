package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ProcurementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
@Repository
public interface ReportDAO extends JpaRepository<ProcurementReport,Integer> {

    @Query("select ref_no,title, tender_type, tender_category_id, open_date, closed_date, status from tender, " +
            "tender_category tc where tc.id = tender_category_id and open_date >= ?1 and open_date <= ?2")
    List<ProcurementReport> findAllByDateRange(String startDate, String endDate);

}
