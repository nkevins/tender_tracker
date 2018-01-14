package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.dao.dto.ReportSummaryDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by fistg on 17-Dec-17.
 */
public interface ReportService {
    List<ProcurementReportDTO> findAllByDateRange(Date openDateFrom, Date openDateTo,
                                                  Date closeDateFrom, Date closeDateTo,
                                                  String category, String status);
    List<ReportSummaryDTO> findTenderSummary(Date startDate, Date endDate);
    List<ReportSummaryDTO> findCompanySummary(Date startDate, Date endDate);

    Integer getNumberOfVisit(int tenderId);
    Integer getNumberOfUniqueVisit(int tenderId);
    List<Object[]> getTopCountryVisitor(int tenderId);
}
