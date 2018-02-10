package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.dao.dto.ReportSummaryDTO;

import java.util.Date;
import java.util.List;

/**
 * Service interface for report.
 */
public interface ReportService {
    /**
     * This method is used to retrieve tender report data based on date range.
     *
     * @param openDateFrom opening date from
     * @param openDateTo opening date to
     * @param closeDateFrom closing date from
     * @param closeDateTo closing date to
     * @param category tender category
     * @param status tender status
     * @return list of report data
     * @see ProcurementReportDTO
     */
    List<ProcurementReportDTO> findAllByDateRange(Date openDateFrom, Date openDateTo,
                                                  Date closeDateFrom, Date closeDateTo,
                                                  String category, String status);

    /**
     * This method is used to get tender summary report data.
     *
     * @param startDate from date
     * @param endDate to date
     * @return list of report data
     * @see ReportSummaryDTO
     */
    List<ReportSummaryDTO> findTenderSummary(Date startDate, Date endDate);

    /**
     * This method is used to get company summary report data.
     *
     * @param startDate from date
     * @param endDate to date
     * @return list of report data
     * @see ReportSummaryDTO
     */
    List<ReportSummaryDTO> findCompanySummary(Date startDate, Date endDate);

    /**
     * This method is used to get number of visitors for a particular tender.
     *
     * @param tenderId unique identifier of the tender.
     * @return integer
     */
    Integer getNumberOfVisit(int tenderId);

    /**
     * This method is used to get number of unique visitors for a particular tender.
     *
     * @param tenderId unique identifier of the tender.
     * @return integer
     */
    Integer getNumberOfUniqueVisit(int tenderId);

    /**
     * This method is used to get the top country visitors for a particular tender.
     *
     * @param tenderId unique identifier of the tender.
     * @return integer
     */
    List<Object[]> getTopCountryVisitor(int tenderId);
}
